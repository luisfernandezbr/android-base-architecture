package com.example.basearch.data.repository

import com.example.basearch.data.ResultWrapper
import com.example.basearch.data.local.mapper.RepoCacheMapper
import com.example.basearch.data.local.preferences.SharedPreferencesUtil
import com.example.basearch.data.local.source.RepoCacheDataSourceImpl
import com.example.basearch.data.remote.BaseErrorData
import com.example.basearch.data.remote.mapper.RepoRemoteMapper
import com.example.basearch.data.remote.source.RepoRemoteDataSourceImpl
import com.example.basearch.domain.entities.Repo
import com.example.basearch.domain.repository.RepoRepository

class RepoRepositoryImpl(
    private val remoteRepository: RepoRemoteDataSourceImpl,
    private val localRepository: RepoCacheDataSourceImpl,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : RepoRepository {
    override fun getLastSyncDate(): String {
        return sharedPreferencesUtil.lastRepoSyncDate
    }

    override suspend fun getAllRemoteRepos(
        page: Int,
        language: String
    ): ResultWrapper<List<Repo>, BaseErrorData<Void>> {
        return when (val remoteResult = remoteRepository.getRepos(page, language)) {
            is ResultWrapper.Success -> {
                ResultWrapper.Success(remoteResult.data.repoPayloads.map { RepoRemoteMapper.map(it) })
            }

            is ResultWrapper.Error -> {
                remoteResult.transformError()
            }
        }
    }

    override suspend fun getAllLocalRepos(): ResultWrapper<List<Repo>?, BaseErrorData<Void>> {
        return when (val localResponse = localRepository.getRepos()) {
            is ResultWrapper.Success -> {
                ResultWrapper.Success(localResponse.data?.map { RepoCacheMapper.map(it) })
            }

            is ResultWrapper.Error -> {
                localResponse.transformError()
            }
        }
    }

    override suspend fun getRepo(id: Int, fromRemote: Boolean): ResultWrapper<Repo?, BaseErrorData<Void>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}