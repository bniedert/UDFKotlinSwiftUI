package com.example.udfapp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ContentViewModel : BaseViewModel<ContentViewState, ContentViewAction>(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override fun initialState() =
        ContentViewState(
            loadingState = LoadingState.None
        )

    override fun onAction(action: ContentViewAction) {
        val oldState = state.value

        state.value =
            ContentViewState(
                loadingState = determineLoadingState(oldState.loadingState, action),
                responses = determineResponses(oldState.responses, action)
            )
    }

    private fun determineLoadingState(state: LoadingState, action: ContentViewAction) =
        when (action) {
            is ContentViewAction.RequestName -> {
                launch { getAge(action.name) }
                LoadingState.Loading
            }
            is ContentViewAction.AgifyResponseReceived -> LoadingState.Loaded
            else -> state
        }

    private fun determineResponses(state: List<AgifyResponse>, action: ContentViewAction) =
        when (action) {
            is ContentViewAction.AgifyResponseReceived -> {
                val mutableList = state.toMutableList()
                mutableList.add(action.response)
                mutableList.toList()
            }
            else -> state
        }

    private suspend fun getAge(name: String) {
        NetworkService.request(name) { response, error ->
            if (error == null) {
                val json = Json { ignoreUnknownKeys = true }
                val parsedResponse = json.decodeFromString<AgifyResponse>(response)
                onAction(ContentViewAction.AgifyResponseReceived(parsedResponse))
            } else {
                println(error)
            }
        }
    }
}

data class ContentViewState(
    val loadingState: LoadingState,
    val responses: List<AgifyResponse> = listOf()
) : ViewState

sealed class ContentViewAction : Action {
    data class RequestName(val name: String) : ContentViewAction()
    data class AgifyResponseReceived(val response: AgifyResponse) : ContentViewAction()
}