package com.ggomodoro.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ggomodoro.domain.model.SessionStatus
import com.ggomodoro.domain.model.TimerSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 히스토리 화면의 루트 Composable입니다.
 * ViewModel에서 데이터 상태를 수집하고 하위 Composable에 전달합니다.
 *
 * @param viewModel 히스토리 ViewModel
 */
@Composable
fun HistoryRoute(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.historyState.collectAsState()
    HistoryScreen(
        history = history,
        onUpdateMemo = viewModel::updateMemo
    )
}

/**
 * 히스토리 목록을 표시하는 화면입니다.
 * 데이터가 없으면 안내 메시지를 표시하고, 있으면 리스트 형태로 보여줍니다.
 *
 * @param history 표시할 타이머 세션 리스트
 * @param onUpdateMemo 메모 업데이트 콜백
 */
@Composable
fun HistoryScreen(
    history: List<TimerSession>,
    onUpdateMemo: (TimerSession, String) -> Unit
) {
    if (history.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No history yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { session ->
                HistoryItem(session = session, onUpdateMemo = onUpdateMemo)
            }
        }
    }
}

/**
 * 개별 히스토리 아이템을 표시하는 카드 UI입니다.
 * 세션 정보(시간, 기간, 상태)를 보여주고 메모를 수정할 수 있습니다.
 *
 * @param session 표시할 세션 정보
 * @param onUpdateMemo 메모 수정 콜백
 */
@Composable
fun HistoryItem(
    session: TimerSession,
    onUpdateMemo: (TimerSession, String) -> Unit
) {
    var showMemoDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (session.status == SessionStatus.SUCCESS) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatDate(session.startTimeEpochMillis),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${session.plannedDurationMinutes} min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Status Icon or Text
                if (session.status == SessionStatus.SUCCESS) {
                     Icon(
                         painter = painterResource(android.R.drawable.star_on), // Placeholder star
                         contentDescription = "Success",
                         tint = MaterialTheme.colorScheme.primary
                     )
                } else {
                    Text(
                        text = "FAILED",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Memo Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMemoDialog = true }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_edit), // Placeholder edit
                    contentDescription = "Edit Memo",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = session.memo.takeIf { !it.isNullOrBlank() } ?: "Add a memo...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (session.memo.isNullOrBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    if (showMemoDialog) {
        MemoEditDialog(
            currentMemo = session.memo ?: "",
            onDismiss = { showMemoDialog = false },
            onConfirm = { newMemo ->
                onUpdateMemo(session, newMemo)
                showMemoDialog = false
            }
        )
    }
}

/**
 * 메모 수정을 위한 다이얼로그입니다.
 *
 * @param currentMemo 현재 메모 내용
 * @param onDismiss 취소 콜백
 * @param onConfirm 저장 콜백
 */
@Composable
fun MemoEditDialog(
    currentMemo: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentMemo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Memo") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 100) text = it },
                label = { Text("Memo (max 100 chars)") },
                singleLine = false,
                maxLines = 3,
                supportingText = { Text("${text.length}/100") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(millis))
}
