import { PredictionEnergyMetadataDataSource } from './prediction-energy-metadata.data-source';

describe('PredictionEnergyMetadataDataSource', () => {
  it('skips Supabase lookup for non-persisted preview load ids', async () => {
    const supabaseService = {
      getClient: jest.fn(() => {
        throw new Error('Supabase should not be queried for preview ids');
      }),
    };
    const dataSource = new PredictionEnergyMetadataDataSource(supabaseService as never);

    await expect(dataSource.resolveForLaundryLoad('user-1', 'new-load-preview')).resolves.toEqual({
      spinRpm: null,
      loadSize: null,
      washerEnergyLabel: null,
      washerCapacityKg: null,
      waterUsageLiters: null,
    });
    expect(supabaseService.getClient).not.toHaveBeenCalled();
  });
});
