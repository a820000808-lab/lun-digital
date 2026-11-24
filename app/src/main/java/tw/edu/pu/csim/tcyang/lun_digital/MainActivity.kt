package tw.edu.pu.csim.tcyang.lun_digital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tw.edu.pu.csim.tcyang.lun_digital.ui.theme.LundigitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LundigitalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Doodle(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Doodle(modifier: Modifier = Modifier) {
    var recognizedText by remember {
        mutableStateOf("請在畫布區手寫，再按「開始辨識」按鈕")
    }
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .border(2.dp, Color.Gray)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val newPath = Path().apply {
                                moveTo(offset.x, offset.y)
                            }
                            currentPath = newPath
                            paths.add(newPath)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPath?.let { path ->
                                val updated = Path().apply {
                                    addPath(path)
                                    lineTo(change.position.x, change.position.y)
                                }
                                paths[paths.lastIndex] = updated
                                currentPath = updated
                            }
                        },
                        onDragEnd = {
                            currentPath = null
                        },
                        onDragCancel = {
                            currentPath = null
                        }
                    )
                }
        ) {
            val strokeStyle = Stroke(
                width = 8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
            paths.forEach { path ->
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = strokeStyle
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            // 開始辨識
            Button(onClick = {
                if (paths.isEmpty()) {
                    recognizedText = "畫布目前沒有筆畫"
                } else {
                    recognizedText = "已收到 ${paths.size} 筆畫，待串接辨識功能"
                }
            }) {
                Text("開始辨識")
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 清除畫布
            Button(onClick = {
                paths.clear()
                currentPath = null
                recognizedText = "請在畫布區手寫，再按「開始辨識」按鈕"
            }) {
                Text("清除")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(recognizedText)
    }
}

@Preview(showBackground = true)
@Composable
fun DoodlePreview() {
    LundigitalTheme {
        Doodle()
    }
}