# scaffold-screen — file skeletons

Replace `Xxx` (PascalCase) and `xxx` (package segment / string prefix). The skeletons assume the domain layer already has:

```kotlin
// SRC/domain/model/XxxItem.kt
data class XxxItem(val id: String, val name: String)

// SRC/domain/repository/XxxRepository.kt
interface XxxRepository {
    suspend fun getItems(): ApiResult<List<XxxItem>>
    suspend fun submit(): ApiResult<Unit>
}
```

Strings used below (add to `app/src/main/res/values/strings.xml`):

```xml
    <string name="xxx_empty_title">No items yet</string>
    <string name="xxx_submit">Submit</string>
    <string name="xxx_submitted">Submitted</string>
```

## `SRC/feature/xxx/XxxUiState.kt`

```kotlin
package com.noshitechinc.restaurant.feature.xxx

import com.noshitechinc.restaurant.core.ui.LoadState
import com.noshitechinc.restaurant.domain.model.XxxItem

data class XxxUiState(
    val items: LoadState<List<XxxItem>> = LoadState.Idle,
)
```

## `SRC/feature/xxx/XxxViewModel.kt`

```kotlin
package com.noshitechinc.restaurant.feature.xxx

import androidx.lifecycle.viewModelScope
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.UiText
import com.noshitechinc.restaurant.core.ui.BaseViewModel
import com.noshitechinc.restaurant.core.ui.LoadState
import com.noshitechinc.restaurant.core.ui.MessageTone
import com.noshitechinc.restaurant.core.ui.ShowMessage
import com.noshitechinc.restaurant.core.ui.toLoadState
import com.noshitechinc.restaurant.domain.repository.XxxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class XxxViewModel @Inject constructor(
    private val repository: XxxRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(XxxUiState())
    val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        _uiState.update { it.copy(items = LoadState.Loading) }
        loadJob = viewModelScope.launch {
            val result = repository.getItems()
            _uiState.update { it.copy(items = result.toLoadState()) }
        }
    }

    fun onSubmitClick() {
        launchSubmit(SUBMIT_KEY) {
            when (val result = repository.submit()) {
                is ApiResult.Success -> {
                    val message = UiText.Resource(R.string.xxx_submitted)
                    sendEffect(ShowMessage(message, MessageTone.Success))
                }
                is ApiResult.Failure -> presentError(result.error)
            }
        }
    }

    companion object {
        const val SUBMIT_KEY = "submit"
    }
}
```

## `SRC/feature/xxx/XxxScreen.kt`

```kotlin
package com.noshitechinc.restaurant.feature.xxx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonSize
import com.noshitechinc.restaurant.core.designsystem.component.row.ListRow
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.state.EmptyState
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.AppScaffold
import com.noshitechinc.restaurant.core.ui.LoadState
import com.noshitechinc.restaurant.core.ui.state.LoadStateContent
import com.noshitechinc.restaurant.domain.model.XxxItem

@Composable
fun XxxRoute(viewModel: XxxViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val submitting by viewModel.submittingKeys.collectAsStateWithLifecycle()
    AppScaffold(
        effects = viewModel.effects,
        requiresNetwork = true,
        onRetryConnection = viewModel::load,
    ) { padding ->
        XxxScreen(
            state = state,
            submitting = XxxViewModel.SUBMIT_KEY in submitting,
            onRetry = viewModel::load,
            onSubmit = viewModel::onSubmitClick,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun XxxScreen(
    state: XxxUiState,
    submitting: Boolean,
    onRetry: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoadStateContent(
        state = state.items,
        onRetry = onRetry,
        empty = { EmptyState(title = stringResource(R.string.xxx_empty_title)) },
        modifier = modifier.fillMaxSize(),
    ) { items ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = AppTheme.sizes.contentMaxWidth)
                    .fillMaxSize()
                    .padding(AppTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = AppTheme.spacing.sm),
                ) {
                    items(items, key = { it.id }) { item -> ListRow(title = item.name) }
                }
                AppButton(
                    text = stringResource(R.string.xxx_submit),
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Large,
                    loading = submitting,
                )
            }
        }
    }
}

private val previewItems = listOf(
    XxxItem(id = "1", name = PreviewData.SHORT_NAME),
    XxxItem(id = "2", name = PreviewData.LONG_NAME),
)

@ScreenPreviews
@Composable
private fun XxxScreenLoadingPreview() {
    PreviewSurface {
        XxxScreen(
            state = XxxUiState(items = LoadState.Loading),
            submitting = false,
            onRetry = {},
            onSubmit = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun XxxScreenContentPreview() {
    PreviewSurface {
        XxxScreen(
            state = XxxUiState(items = LoadState.Content(previewItems)),
            submitting = false,
            onRetry = {},
            onSubmit = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun XxxScreenSubmittingPreview() {
    PreviewSurface {
        XxxScreen(
            state = XxxUiState(items = LoadState.Content(previewItems)),
            submitting = true,
            onRetry = {},
            onSubmit = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun XxxScreenEmptyPreview() {
    PreviewSurface {
        XxxScreen(
            state = XxxUiState(items = LoadState.Empty),
            submitting = false,
            onRetry = {},
            onSubmit = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun XxxScreenErrorPreview() {
    PreviewSurface {
        XxxScreen(
            state = XxxUiState(items = LoadState.Error(AppError.Timeout)),
            submitting = false,
            onRetry = {},
            onSubmit = {},
        )
    }
}
```

`previewItems` sits after the Route and Screen and before the first preview annotation; keep only preview data there.

## Navigation

`SRC/navigation/Destinations.kt` (append):

```kotlin
@Serializable
data object XxxDestination
```

`SRC/navigation/AppNavHost.kt` (inside `NavHost { … }`, plus `import com.noshitechinc.restaurant.feature.xxx.XxxRoute`):

```kotlin
        composable<XxxDestination> { XxxRoute() }
```

## `TEST/fakes/FakeXxxRepository.kt`

```kotlin
package com.noshitechinc.restaurant.fakes

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.domain.model.XxxItem
import com.noshitechinc.restaurant.domain.repository.XxxRepository
import kotlinx.coroutines.CompletableDeferred

class FakeXxxRepository : XxxRepository {
    var itemsResult: ApiResult<List<XxxItem>> = ApiResult.Success(emptyList())
    var submitResult: ApiResult<Unit> = ApiResult.Success(Unit)
    var submitGate: CompletableDeferred<Unit>? = null
    var submitCalls = 0
        private set

    override suspend fun getItems(): ApiResult<List<XxxItem>> = itemsResult

    override suspend fun submit(): ApiResult<Unit> {
        submitCalls++
        submitGate?.await()
        return submitResult
    }
}
```

## `TEST/feature/xxx/XxxViewModelTest.kt`

```kotlin
package com.noshitechinc.restaurant.feature.xxx

import app.cash.turbine.test
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.common.UiText
import com.noshitechinc.restaurant.core.ui.LoadState
import com.noshitechinc.restaurant.core.ui.MessageTone
import com.noshitechinc.restaurant.core.ui.ShowErrorDialog
import com.noshitechinc.restaurant.core.ui.ShowMessage
import com.noshitechinc.restaurant.domain.model.XxxItem
import com.noshitechinc.restaurant.fakes.FakeXxxRepository
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class XxxViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeXxxRepository()
    private val items = listOf(XxxItem("1", "Latte"))

    @Test
    fun `loaded items become Content`() {
        repository.itemsResult = ApiResult.Success(items)
        assertEquals(LoadState.Content(items), XxxViewModel(repository).uiState.value.items)
    }

    @Test
    fun `an empty list becomes Empty`() {
        assertEquals(LoadState.Empty, XxxViewModel(repository).uiState.value.items)
    }

    @Test
    fun `a load failure becomes Error`() {
        repository.itemsResult = ApiResult.Failure(AppError.Timeout)
        val vm = XxxViewModel(repository)
        assertEquals(LoadState.Error(AppError.Timeout), vm.uiState.value.items)
    }

    @Test
    fun `a second submit tap is ignored while the first runs`() = runTest {
        val gate = CompletableDeferred<Unit>()
        repository.submitGate = gate
        val vm = XxxViewModel(repository)
        vm.onSubmitClick()
        vm.onSubmitClick()
        assertEquals(setOf(XxxViewModel.SUBMIT_KEY), vm.submittingKeys.value)
        gate.complete(Unit)
        assertEquals(1, repository.submitCalls)
        assertEquals(emptySet(), vm.submittingKeys.value)
    }

    @Test
    fun `a successful submit shows a success message`() = runTest {
        val vm = XxxViewModel(repository)
        vm.effects.test {
            vm.onSubmitClick()
            val expected = ShowMessage(UiText.Resource(R.string.xxx_submitted), MessageTone.Success)
            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `a failed submit shows an error dialog`() = runTest {
        repository.submitResult = ApiResult.Failure(AppError.Forbidden)
        val vm = XxxViewModel(repository)
        vm.effects.test {
            vm.onSubmitClick()
            assertEquals(ShowErrorDialog(AppError.Forbidden), awaitItem())
        }
    }
}
```
