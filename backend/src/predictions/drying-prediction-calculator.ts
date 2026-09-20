import { DryingLocationProfile, getDryingLocationProfile } from '../drying-locations/drying-location-types';
import {
  EstimatedWashingCostConfidence,
  EstimatedWashingCostLevel,
  LoadSize,
  SpinSpeedRpm,
} from '../energy/energy-prediction-types';
import { ClothingType } from '../laundry-loads/dto/laundry-load-types';
import { WashingProgram } from '../laundry-loads/dto/laundry-load-types';
import { WeatherSnapshotResponseDto } from '../weather/dto/weather-snapshot-response.dto';
import { WasherEnergyLabel } from '../washers/dto/washer-energy-label';
import { DryingMethod, DryingVerdict } from './dto/drying-prediction-types';

export interface DryingCalculationInput {
  clothingType: ClothingType;
  washingProgram: WashingProgram;
  dryingMethod: DryingMethod;
  dryingLocationId: DryingLocationProfile['id'];
  weather: WeatherSnapshotResponseDto;
  forecast?: WeatherSnapshotResponseDto[] | null;
  earliestHangAt?: Date | string | null;
  spinRpm?: SpinSpeedRpm | null;
  loadSize?: LoadSize | null;
  washerEnergyLabel?: WasherEnergyLabel | null;
  washerCapacityKg?: number | null;
  waterUsageLiters?: number | null;
}

export interface EstimatedWashingCostResult {
  amount: number | null;
  currency: string | null;
  level: EstimatedWashingCostLevel;
  confidence: EstimatedWashingCostConfidence;
  estimatedEnergyKwh: number | null;
  estimatedWaterLiters: number | null;
}

export interface DryingHourlySlotResult {
  forecastFor: string;
  verdict: DryingVerdict;
  suitabilityScore: number;
  temperatureCelsius: number;
  humidityPercent: number;
  windSpeedKph: number;
  rainProbabilityPercent: number;
}

export interface DryingCalculationResult {
  verdict: DryingVerdict;
  dryingMethod: DryingMethod;
  dryingLocationId: DryingLocationProfile['id'];
  dryingLocationLabel: string;
  estimatedDryingMinutes: number;
  recommendedHangAt: string;
  recommendedHangWindowStart: string;
  recommendedHangWindowEnd: string;
  estimatedPickupAt: string;
  suitabilityScore: number;
  reason: string;
  estimatedCost: EstimatedWashingCostResult;
  hourlySlots: DryingHourlySlotResult[];
}

export class DryingPredictionCalculator {
  calculate(input: DryingCalculationInput): DryingCalculationResult {
    const dryingLocation = getDryingLocationProfile(input.dryingLocationId);
    const forecastWindow = normalizeForecastWindow(input.weather, input.forecast ?? null);
    const score = this.calculateScore(input.weather, input.dryingMethod, dryingLocation);
    const verdict = this.selectVerdict(score, input.weather, input.dryingMethod, dryingLocation);
    const estimatedCost = this.estimateWashingCost(input);
    const hourlySlots = forecastWindow
      .slice(0, CONFIG.maximumHourlySlots)
      .map((snapshot) => this.createHourlySlot(snapshot, input.dryingMethod, dryingLocation));
    const recommendedHangWindow = selectRecommendedHangWindow(
      hourlySlots,
      input.weather.forecastFor,
      normalizeEarliestHangAt(input.earliestHangAt),
    );
    const recommendedHangAt = new Date(recommendedHangWindow.start);
    const dryingForecastWindow = selectForecastWindowFrom(forecastWindow, recommendedHangAt);
    const dryingWeather = dryingForecastWindow[0] ?? input.weather;
    const dryingScore = this.calculateScore(dryingWeather, input.dryingMethod, dryingLocation);
    const dryingVerdict = this.selectVerdict(dryingScore, dryingWeather, input.dryingMethod, dryingLocation);
    const estimatedDryingMinutes = this.estimateDryingMinutes(
      input.clothingType,
      dryingWeather,
      input.dryingMethod,
      dryingLocation,
      dryingVerdict,
      input.spinRpm ?? null,
      input.loadSize ?? null,
      dryingForecastWindow,
    );
    const estimatedPickupAt = new Date(recommendedHangAt.getTime() + estimatedDryingMinutes * MILLIS_PER_MINUTE);

    return {
      verdict,
      dryingMethod: input.dryingMethod,
      dryingLocationId: dryingLocation.id,
      dryingLocationLabel: dryingLocation.label,
      estimatedDryingMinutes,
      recommendedHangAt: recommendedHangAt.toISOString(),
      recommendedHangWindowStart: recommendedHangWindow.start,
      recommendedHangWindowEnd: recommendedHangWindow.end,
      estimatedPickupAt: estimatedPickupAt.toISOString(),
      suitabilityScore: score,
      reason: this.buildReason(verdict, input.weather, input.dryingMethod, dryingLocation, input.spinRpm ?? null, input.loadSize ?? null, estimatedCost),
      estimatedCost,
      hourlySlots,
    };
  }

  private createHourlySlot(
    weather: WeatherSnapshotResponseDto,
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
  ): DryingHourlySlotResult {
    const suitabilityScore = this.calculateScore(weather, method, dryingLocation);

    return {
      forecastFor: weather.forecastFor,
      verdict: this.selectVerdict(suitabilityScore, weather, method, dryingLocation),
      suitabilityScore,
      temperatureCelsius: weather.temperatureCelsius,
      humidityPercent: weather.humidityPercent,
      windSpeedKph: weather.windSpeedKph,
      rainProbabilityPercent: weather.rainProbabilityPercent,
    };
  }

  private calculateScore(
    weather: WeatherSnapshotResponseDto,
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
  ): number {
    const rawRainPenalty =
      method === 'OUTDOOR' ? weather.rainProbabilityPercent : Math.floor(weather.rainProbabilityPercent / 3);
    const rainPenalty = Math.round(rawRainPenalty * dryingLocation.rainExposureFactor);
    const humidityPenalty = Math.floor(
      (Math.max(weather.humidityPercent - CONFIG.idealHumidityPercent, 0) / 2) *
        dryingLocation.humidityRetentionFactor,
    );
    const cloudPenalty = Math.floor(weather.cloudCoverPercent / 5);
    const coldPenalty = Math.max(Math.trunc((CONFIG.idealTemperatureCelsius - weather.temperatureCelsius) * 2), 0);
    const windBonus = Math.trunc(Math.min(weather.windSpeedKph, CONFIG.maxUsefulWindKph) * dryingLocation.airflowFactor);

    return clamp(CONFIG.baseScore - rainPenalty - humidityPenalty - cloudPenalty - coldPenalty + windBonus, CONFIG.minimumScore, CONFIG.maximumScore);
  }

  private selectVerdict(
    score: number,
    weather: WeatherSnapshotResponseDto,
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
  ): DryingVerdict {
    const effectiveRainRisk = weather.rainProbabilityPercent * dryingLocation.rainExposureFactor;
    if (method === 'OUTDOOR' && effectiveRainRisk >= CONFIG.badRainProbabilityPercent) {
      return 'BAD';
    }

    if (score >= CONFIG.goodScoreThreshold) {
      return 'GOOD';
    }

    if (score >= CONFIG.cautionScoreThreshold) {
      return 'CAUTION';
    }

    return 'BAD';
  }

  private estimateDryingMinutes(
    clothingType: ClothingType,
    weather: WeatherSnapshotResponseDto,
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
    verdict: DryingVerdict,
    spinRpm: SpinSpeedRpm | null,
    loadSize: LoadSize | null,
    forecast: WeatherSnapshotResponseDto[] | null = null,
  ): number {
    const forecastWindow = normalizeForecastWindow(weather, forecast);
    if (forecastWindow.length > 1) {
      return this.estimateSegmentedDryingMinutes(
        clothingType,
        forecastWindow,
        method,
        dryingLocation,
        spinRpm,
        loadSize,
      );
    }

    return this.estimateSingleSnapshotDryingMinutes(
      clothingType,
      weather,
      method,
      dryingLocation,
      verdict,
      spinRpm,
      loadSize,
    );
  }

  private estimateSegmentedDryingMinutes(
    clothingType: ClothingType,
    forecastWindow: WeatherSnapshotResponseDto[],
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
    spinRpm: SpinSpeedRpm | null,
    loadSize: LoadSize | null,
  ): number {
    let remainingDryingWork = 1.0;
    let elapsedMinutes = 0;

    for (let index = 0; index < forecastWindow.length; index += 1) {
      const snapshot = forecastWindow[index];
      const snapshotScore = this.calculateScore(snapshot, method, dryingLocation);
      const snapshotVerdict = this.selectVerdict(snapshotScore, snapshot, method, dryingLocation);
      const snapshotDryingMinutes = this.estimateSingleSnapshotDryingMinutes(
        clothingType,
        snapshot,
        method,
        dryingLocation,
        snapshotVerdict,
        spinRpm,
        loadSize,
      );
      const stepMinutes = forecastStepMinutes(forecastWindow, index);
      const completedWork = stepMinutes / snapshotDryingMinutes;

      if (completedWork >= remainingDryingWork) {
        return Math.max(
          Math.ceil(elapsedMinutes + remainingDryingWork * snapshotDryingMinutes),
          CONFIG.minimumDryingMinutes,
        );
      }

      remainingDryingWork -= completedWork;
      elapsedMinutes += stepMinutes;
    }

    const lastSnapshot = forecastWindow[forecastWindow.length - 1];
    const lastScore = this.calculateScore(lastSnapshot, method, dryingLocation);
    const lastVerdict = this.selectVerdict(lastScore, lastSnapshot, method, dryingLocation);
    const lastDryingMinutes = this.estimateSingleSnapshotDryingMinutes(
      clothingType,
      lastSnapshot,
      method,
      dryingLocation,
      lastVerdict,
      spinRpm,
      loadSize,
    );

    return Math.max(Math.ceil(elapsedMinutes + remainingDryingWork * lastDryingMinutes), CONFIG.minimumDryingMinutes);
  }

  private estimateSingleSnapshotDryingMinutes(
    clothingType: ClothingType,
    weather: WeatherSnapshotResponseDto,
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
    verdict: DryingVerdict,
    spinRpm: SpinSpeedRpm | null,
    loadSize: LoadSize | null,
  ): number {
    const baseMinutes = BASE_DRYING_MINUTES[clothingType];
    const methodMultiplier = method === 'INDOOR' ? 1.35 : 1.0;
    const humidityMultiplier = 1.0 + Math.max(weather.humidityPercent, 0) / 200.0;
    const locationMultiplier = dryingLocation.humidityRetentionFactor / Math.max(dryingLocation.airflowFactor, 0.25);
    const verdictMultiplier = VERDICT_MULTIPLIERS[verdict];
    const spinMultiplier = SPIN_RESIDUAL_MOISTURE_FACTORS[spinRpm ?? 'UNKNOWN'];
    const loadSizeMultiplier = LOAD_SIZE_MULTIPLIERS[loadSize ?? 'MEDIUM'];

    return Math.max(
      Math.trunc(
        baseMinutes *
          methodMultiplier *
          humidityMultiplier *
          locationMultiplier *
          verdictMultiplier *
          spinMultiplier *
          loadSizeMultiplier,
      ),
      CONFIG.minimumDryingMinutes,
    );
  }

  private estimateWashingCost(input: DryingCalculationInput): EstimatedWashingCostResult {
    const loadSize = input.loadSize ?? 'MEDIUM';
    const energyLabel = input.washerEnergyLabel ?? 'UNKNOWN';
    const programFactor = PROGRAM_COST_FACTORS[input.washingProgram];
    const energyFactor = ENERGY_LABEL_COST_FACTORS[energyLabel];
    const loadFactor = LOAD_SIZE_COST_FACTORS[loadSize];
    const capacityFactor = input.washerCapacityKg ? clamp(input.washerCapacityKg / CONFIG.referenceCapacityKg, 0.75, 1.35) : 1;
    const estimatedEnergyKwh = roundToTwoDecimals(CONFIG.referenceEnergyKwh * programFactor * energyFactor * loadFactor * capacityFactor);
    const estimatedWaterLiters = roundToOneDecimal(
      (input.waterUsageLiters ?? PROGRAM_BASE_WATER_LITERS[input.washingProgram]) * loadFactor,
    );
    const level = this.selectCostLevel(estimatedEnergyKwh);
    const confidence = this.selectCostConfidence(input.washerEnergyLabel ?? null, input.waterUsageLiters ?? null);

    return {
      amount: null,
      currency: null,
      level,
      confidence,
      estimatedEnergyKwh,
      estimatedWaterLiters,
    };
  }

  private selectCostLevel(estimatedEnergyKwh: number): EstimatedWashingCostLevel {
    if (estimatedEnergyKwh < CONFIG.lowCostEnergyKwh) {
      return 'LOW';
    }

    if (estimatedEnergyKwh > CONFIG.highCostEnergyKwh) {
      return 'HIGH';
    }

    return 'MEDIUM';
  }

  private selectCostConfidence(
    washerEnergyLabel: WasherEnergyLabel | null,
    waterUsageLiters: number | null,
  ): EstimatedWashingCostConfidence {
    if (washerEnergyLabel && waterUsageLiters) {
      return 'HIGH';
    }

    if (washerEnergyLabel || waterUsageLiters) {
      return 'MEDIUM';
    }

    return 'LOW';
  }

  private buildReason(
    verdict: DryingVerdict,
    weather: WeatherSnapshotResponseDto,
    method: DryingMethod,
    dryingLocation: DryingLocationProfile,
    spinRpm: SpinSpeedRpm | null,
    loadSize: LoadSize | null,
    estimatedCost: EstimatedWashingCostResult,
  ): string {
    const effectiveRainRisk = weather.rainProbabilityPercent * dryingLocation.rainExposureFactor;
    if (method === 'OUTDOOR' && effectiveRainRisk >= CONFIG.badRainProbabilityPercent) {
      return this.appendEnergyReason(
        `Rain risk is high for ${dryingLocation.label}, so outdoor drying is not recommended.`,
        spinRpm,
        loadSize,
        estimatedCost,
      );
    }

    let reason: string;
    switch (verdict) {
      case 'GOOD':
        reason = `Weather is favorable for drying in ${dryingLocation.label}: low rain risk and useful airflow.`;
        break;
      case 'CAUTION':
        reason = `Drying in ${dryingLocation.label} is possible, but humidity, exposure or clouds may slow it down.`;
        break;
      case 'BAD':
        reason = `Conditions are weak for ${dryingLocation.label} because humidity, rain or cold are high.`;
        break;
    }

    return this.appendEnergyReason(reason, spinRpm, loadSize, estimatedCost);
  }

  private appendEnergyReason(
    baseReason: string,
    spinRpm: SpinSpeedRpm | null,
    loadSize: LoadSize | null,
    estimatedCost: EstimatedWashingCostResult,
  ): string {
    const details: string[] = [];

    if (spinRpm && spinRpm >= 1200) {
      details.push('High spin speed reduces estimated residual moisture.');
    } else if (spinRpm && spinRpm <= 800) {
      details.push('Low spin speed may leave more residual moisture.');
    }

    if (loadSize === 'LARGE') {
      details.push('Large loads increase the estimated drying time and consumption.');
    } else if (loadSize === 'SMALL') {
      details.push('Small loads reduce the estimated drying time and consumption.');
    }

    details.push(`Approximate washing cost is ${estimatedCost.level.toLowerCase()} confidence ${estimatedCost.confidence.toLowerCase()}.`);

    return `${baseReason} ${details.join(' ')}`;
  }
}

const MILLIS_PER_MINUTE = 60_000;

const CONFIG = {
  baseScore: 85,
  minimumScore: 0,
  maximumScore: 100,
  idealHumidityPercent: 45,
  idealTemperatureCelsius: 20,
  maxUsefulWindKph: 20,
  goodScoreThreshold: 70,
  cautionScoreThreshold: 40,
  badRainProbabilityPercent: 70,
  minimumDryingMinutes: 60,
  referenceCapacityKg: 7,
  referenceEnergyKwh: 0.9,
  lowCostEnergyKwh: 0.75,
  highCostEnergyKwh: 1.05,
  defaultForecastStepMinutes: 60,
  minimumForecastStepMinutes: 30,
  maximumForecastStepMinutes: 180,
  maximumHourlySlots: 8,
} as const;

const BASE_DRYING_MINUTES: Record<ClothingType, number> = {
  LIGHT_CLOTHES: 120,
  MIXED: 180,
  DELICATES: 150,
  HEAVY_CLOTHES: 260,
  BEDDING: 300,
};

const VERDICT_MULTIPLIERS: Record<DryingVerdict, number> = {
  GOOD: 0.9,
  CAUTION: 1.15,
  BAD: 1.35,
};

const SPIN_RESIDUAL_MOISTURE_FACTORS: Record<SpinSpeedRpm | 'UNKNOWN', number> = {
  UNKNOWN: 1.0,
  600: 1.18,
  800: 1.1,
  1000: 1.04,
  1200: 0.96,
  1400: 0.9,
  1600: 0.86,
};

const LOAD_SIZE_MULTIPLIERS: Record<LoadSize, number> = {
  SMALL: 0.85,
  MEDIUM: 1.0,
  LARGE: 1.18,
};

const LOAD_SIZE_COST_FACTORS: Record<LoadSize, number> = {
  SMALL: 0.85,
  MEDIUM: 1.0,
  LARGE: 1.18,
};

const ENERGY_LABEL_COST_FACTORS: Record<WasherEnergyLabel | 'UNKNOWN', number> = {
  UNKNOWN: 1.0,
  'A+++': 0.72,
  'A++': 0.78,
  'A+': 0.84,
  A: 0.92,
  B: 1.0,
  C: 1.08,
  D: 1.16,
  E: 1.25,
  F: 1.35,
  G: 1.48,
};

const PROGRAM_COST_FACTORS: Record<WashingProgram, number> = {
  QUICK: 0.75,
  NORMAL: 1.0,
  ECO: 0.82,
  DELICATE: 0.9,
};

const PROGRAM_BASE_WATER_LITERS: Record<WashingProgram, number> = {
  QUICK: 38,
  NORMAL: 52,
  ECO: 42,
  DELICATE: 40,
};

function clamp(value: number, minimum: number, maximum: number): number {
  return Math.min(Math.max(value, minimum), maximum);
}

function roundToTwoDecimals(value: number): number {
  return Math.round(value * 100) / 100;
}

function roundToOneDecimal(value: number): number {
  return Math.round(value * 10) / 10;
}

function normalizeForecastWindow(
  weather: WeatherSnapshotResponseDto,
  forecast: WeatherSnapshotResponseDto[] | null,
): WeatherSnapshotResponseDto[] {
  const snapshots = [weather, ...(forecast ?? [])]
    .filter((snapshot) => typeof snapshot.forecastFor === 'string' && snapshot.forecastFor.trim().length > 0)
    .sort((left, right) => new Date(left.forecastFor).getTime() - new Date(right.forecastFor).getTime());

  const uniqueByForecastFor = new Map<string, WeatherSnapshotResponseDto>();
  for (const snapshot of snapshots) {
    if (!uniqueByForecastFor.has(snapshot.forecastFor)) {
      uniqueByForecastFor.set(snapshot.forecastFor, snapshot);
    }
  }

  return Array.from(uniqueByForecastFor.values());
}

function forecastStepMinutes(forecastWindow: WeatherSnapshotResponseDto[], index: number): number {
  const current = new Date(forecastWindow[index].forecastFor).getTime();
  const next = new Date(forecastWindow[index + 1]?.forecastFor ?? '').getTime();

  if (!Number.isFinite(current) || !Number.isFinite(next) || next <= current) {
    return CONFIG.defaultForecastStepMinutes;
  }

  const minutes = Math.round((next - current) / MILLIS_PER_MINUTE);
  return clamp(minutes, CONFIG.minimumForecastStepMinutes, CONFIG.maximumForecastStepMinutes);
}

function selectForecastWindowFrom(
  forecastWindow: WeatherSnapshotResponseDto[],
  start: Date,
): WeatherSnapshotResponseDto[] {
  if (forecastWindow.length === 0) {
    return [];
  }

  const startTime = start.getTime();
  let startIndex = forecastWindow.findIndex((snapshot, index) => {
    const currentTime = new Date(snapshot.forecastFor).getTime();
    const nextTime = new Date(forecastWindow[index + 1]?.forecastFor ?? '').getTime();
    return Number.isFinite(currentTime)
      && currentTime <= startTime
      && (!Number.isFinite(nextTime) || startTime < nextTime);
  });

  if (startIndex < 0) {
    startIndex = forecastWindow.findIndex(
      (snapshot) => new Date(snapshot.forecastFor).getTime() >= startTime,
    );
  }

  return forecastWindow.slice(startIndex >= 0 ? startIndex : forecastWindow.length - 1);
}

function selectRecommendedHangWindow(
  hourlySlots: DryingHourlySlotResult[],
  fallbackStart: string,
  now: Date,
): { start: string; end: string } {
  const nowTime = now.getTime();
  if (hourlySlots.length === 0) {
    const fallbackTime = new Date(fallbackStart).getTime();
    const start = Number.isFinite(fallbackTime) && fallbackTime > nowTime
      ? new Date(fallbackTime)
      : now;
    return {
      start: start.toISOString(),
      end: new Date(start.getTime() + CONFIG.defaultForecastStepMinutes * MILLIS_PER_MINUTE).toISOString(),
    };
  }

  const candidateIndexes = hourlySlots
    .map((slot, index) => ({ slot, index, endTime: calculateWindowEndTime(hourlySlots, index) }))
    .filter((candidate) => candidate.endTime > nowTime);

  if (candidateIndexes.length === 0) {
    return {
      start: now.toISOString(),
      end: new Date(nowTime + CONFIG.defaultForecastStepMinutes * MILLIS_PER_MINUTE).toISOString(),
    };
  }

  const firstGoodCandidate = candidateIndexes.find((candidate) => candidate.slot.verdict === 'GOOD');
  const startIndex = firstGoodCandidate?.index ?? bestAvailableSlotIndex(candidateIndexes.map((candidate) => candidate.slot));
  const selectedIndex = firstGoodCandidate?.index ?? candidateIndexes[startIndex].index;
  let endIndex = selectedIndex;

  for (let index = selectedIndex + 1; index < hourlySlots.length; index += 1) {
    const slot = hourlySlots[index];
    if (slot.suitabilityScore < CONFIG.cautionScoreThreshold || slot.rainProbabilityPercent >= CONFIG.badRainProbabilityPercent) {
      break;
    }
    endIndex = index;
  }

  const selectedStartTime = new Date(hourlySlots[selectedIndex].forecastFor).getTime();
  const startTime = Number.isFinite(selectedStartTime) ? Math.max(selectedStartTime, nowTime) : nowTime;
  const endTime = Math.max(
    calculateWindowEndTime(hourlySlots, endIndex),
    startTime + CONFIG.defaultForecastStepMinutes * MILLIS_PER_MINUTE,
  );

  return {
    start: new Date(startTime).toISOString(),
    end: new Date(endTime).toISOString(),
  };
}

function bestAvailableSlotIndex(hourlySlots: DryingHourlySlotResult[]): number {
  return hourlySlots.reduce((bestIndex, slot, index) => {
    const bestSlot = hourlySlots[bestIndex];
    return slot.suitabilityScore > bestSlot.suitabilityScore ? index : bestIndex;
  }, 0);
}

function calculateWindowEnd(hourlySlots: DryingHourlySlotResult[], endIndex: number): string {
  return new Date(calculateWindowEndTime(hourlySlots, endIndex)).toISOString();
}

function calculateWindowEndTime(hourlySlots: DryingHourlySlotResult[], endIndex: number): number {
  const current = new Date(hourlySlots[endIndex].forecastFor).getTime();
  const next = new Date(hourlySlots[endIndex + 1]?.forecastFor ?? '').getTime();
  return Number.isFinite(next) && next > current
    ? next
    : current + CONFIG.defaultForecastStepMinutes * MILLIS_PER_MINUTE;
}

function normalizeEarliestHangAt(value: Date | string | null | undefined): Date {
  if (value === undefined || value === null) {
    return new Date();
  }

  const instant = value instanceof Date ? new Date(value.getTime()) : new Date(value);
  return Number.isFinite(instant.getTime()) ? instant : new Date();
}
