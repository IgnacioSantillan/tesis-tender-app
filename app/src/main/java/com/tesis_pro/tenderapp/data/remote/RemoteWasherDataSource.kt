package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.SaveWasherRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.WasherResponseDto
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.Washer
import com.tesis_pro.tenderapp.domain.model.WasherType
import java.io.IOException
import retrofit2.HttpException

class RemoteWasherDataSource(
    private val api: TenderBackendApi,
) {
    suspend fun listWashers(accessToken: String): RemoteDataResult<List<Washer>> {
        return safeRemoteCall {
            api.listWashers(accessToken.toBearerHeader()).map { it.toDomain() }
        }
    }

    suspend fun createWasher(
        accessToken: String,
        request: CreateRemoteWasher,
    ): RemoteDataResult<Washer> {
        return safeRemoteCall {
            api.createWasher(
                authorization = accessToken.toBearerHeader(),
                request = request.toDto(),
            ).toDomain()
        }
    }

    suspend fun updateWasher(
        accessToken: String,
        washerId: String,
        request: CreateRemoteWasher,
    ): RemoteDataResult<Washer> {
        return safeRemoteCall {
            api.updateWasher(
                authorization = accessToken.toBearerHeader(),
                washerId = washerId,
                request = request.toDto(),
            ).toDomain()
        }
    }

    suspend fun retireWasher(
        accessToken: String,
        washerId: String,
    ): RemoteDataResult<Unit> {
        return safeRemoteCall {
            api.retireWasher(
                authorization = accessToken.toBearerHeader(),
                washerId = washerId,
            )
        }
    }

    private suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteDataResult<T> {
        return try {
            RemoteDataResult.Success(block())
        } catch (exception: MissingWasherAccessTokenException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Access token is required for protected washer calls.",
            )
        } catch (exception: IllegalArgumentException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned invalid washer data.",
            )
        } catch (exception: HttpException) {
            exception.toRemoteError()
        } catch (exception: IOException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend.",
            )
        } catch (exception: Exception) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.UNKNOWN,
                message = "Unexpected washer data error.",
            )
        }
    }
}

data class CreateRemoteWasher(
    val name: String,
    val type: WasherType,
    val capacityKg: Double?,
    val energyLabel: String?,
    val waterUsageLiters: Double?,
    val defaultSpinRpm: SpinSpeedRpm? = null,
    val isPrimary: Boolean,
)

private fun CreateRemoteWasher.toDto(): SaveWasherRequestDto {
    return SaveWasherRequestDto(
        name = name,
        type = type.name,
        capacityKg = capacityKg,
        energyLabel = energyLabel,
        waterUsageLiters = waterUsageLiters,
        defaultSpinRpm = defaultSpinRpm?.rpm,
        isPrimary = isPrimary,
    )
}

private fun WasherResponseDto.toDomain(): Washer {
    return Washer(
        id = id,
        name = name,
        type = type.toWasherType(),
        capacityKg = capacityKg,
        energyLabel = energyLabel,
        waterUsageLiters = waterUsageLiters,
        isPrimary = isPrimary,
        defaultSpinRpm = SpinSpeedRpm.fromRpm(defaultSpinRpm),
    )
}

private fun String.toWasherType(): WasherType {
    val normalized = trim()
        .uppercase()
        .replace(Regex("[^A-Z0-9]+"), "_")
        .trim('_')

    return when (normalized) {
        "FRONT_LOAD",
        "FRONT",
        "FRONTLOAD",
        "FRONT_LOADING",
        "CARGA_FRONTAL",
        "LAVARROPAS_CARGA_FRONTAL" -> WasherType.FRONT_LOAD

        "TOP_LOAD",
        "TOP",
        "TOPLOAD",
        "TOP_LOADING",
        "CARGA_SUPERIOR",
        "LAVARROPAS_CARGA_SUPERIOR" -> WasherType.TOP_LOAD

        "WASHER_DRYER",
        "WASHERDRYER",
        "DRYER",
        "SECARROPAS",
        "LAVARROPAS_SECARROPAS",
        "LAVASECARROPAS" -> WasherType.WASHER_DRYER

        "OTHER",
        "OTRO" -> WasherType.OTHER

        else -> enumValueOf(normalized)
    }
}

private fun String.toBearerHeader(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) {
        throw MissingWasherAccessTokenException()
    }
    return "Bearer $trimmed"
}

private fun HttpException.toRemoteError(): RemoteDataResult.Error {
    return when (code()) {
        401, 403 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.AUTHENTICATION,
            message = "Authentication is required to load washers.",
        )
        404 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.NOT_FOUND,
            message = "Washer was not found.",
        )
        in 500..599 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.SERVER,
            message = "TenderApp backend could not process washer data.",
        )
        else -> RemoteDataResult.Error(
            type = RemoteDataErrorType.UNKNOWN,
            message = "Backend rejected the washer request.",
        )
    }
}

private class MissingWasherAccessTokenException : RuntimeException()
