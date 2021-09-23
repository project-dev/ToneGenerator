package jp.krohigewagma.tonegenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity(){

    /**
     * 音源
     */
    private val toneGenerator = ToneController(22050, 1, 8)

    /**
     * UIのボタンととOSCObjectのマッピング
     */
    var keyMap = mutableMapOf<Int, MutableList<Int>>()

    /**
     * 音楽データ：
     */
    var trackData = listOf<List<Note>>(
            // トラック1
            listOf<Note>(
                    Note(Tone.F3, 4, 0),
                    Note(Tone.F3, 4, 0),
                    Note(Tone.G3, 2, 0),

                    Note(Tone.F3, 4, 0),
                    Note(Tone.F3, 4, 0),
                    Note(Tone.G3, 2, 0),

                    Note(Tone.F3, 4, 0),
                    Note(Tone.F3, 4, 0),
                    Note(Tone.G3s, 4, 0),
                    Note(Tone.G3, 4, 0),

                    Note(Tone.F3, 4, 0),
                    Note(Tone.G3, 8, 0),
                    Note(Tone.F3, 8, 0),

                    Note(Tone.C3s, 2, 0),

                    Note(Tone.C3, 4, 0),
                    Note(Tone.G2s, 4, 0),
                    Note(Tone.C3, 4, 0),
                    Note(Tone.C3s, 4, 0),

                    Note(Tone.C3, 4, 0),
                    Note(Tone.C3, 8, 0),
                    Note(Tone.G2s, 8, 0),
                    Note(Tone.G2, 4, 0),
            ),
            listOf<Note>(
                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),
                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),

                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),
                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),

                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),
                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),

                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),
                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),

                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),
                    Note(Tone.D2s, 8, 2),
                    Note(Tone.D1s, 8, 2),
                    Note(Tone.C2s, 8, 2),
                    Note(Tone.C1s, 8, 2),
                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),

                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),
                    Note(Tone.F2, 8, 2),
                    Note(Tone.F1, 8, 2),

                    Note(Tone.C2s, 8, 2),
                    Note(Tone.C1s, 8, 2),
                    Note(Tone.C2s, 8, 2),
                    Note(Tone.C1s, 8, 2),

                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),
                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),

                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),
                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),

                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),
                    Note(Tone.C2, 8, 2),
                    Note(Tone.C1, 8, 2),

                    Note(Tone.G2, 8, 2),
                    Note(Tone.G1, 8, 2),
                    Note(Tone.G2, 8, 2),
                    Note(Tone.G1, 8, 2),
            )
    )

    var startNote = mutableListOf<Long>(0L, 0L)

    /**
     * トラックのカウンタ
     */
    var trackCntList = mutableListOf<Int>(0, 0)

    var curNoteList = mutableListOf<Note?>(null, null)

    /**
     * 再生中フラグ
     */
    var isPlay = false

    /**
     * 4分音符の長さ(マイクロ秒)
     */
    var step = 60 * 1000 / 120

    /**
     * ボタンと音のマッピング
     */
    private var toneMap = mapOf(
            Pair(R.id.btnC1, Tone.C1),
            Pair(R.id.btnC1s, Tone.C1s),
            Pair(R.id.btnD1, Tone.D1),
            Pair(R.id.btnD1s, Tone.D1s),
            Pair(R.id.btnE1, Tone.E1),
            Pair(R.id.btnF1, Tone.F1),
            Pair(R.id.btnF1s, Tone.F1s),
            Pair(R.id.btnG1, Tone.G1),
            Pair(R.id.btnG1s, Tone.G1s),
            Pair(R.id.btnA1, Tone.A1),
            Pair(R.id.btnA1s, Tone.A1s),
            Pair(R.id.btnB1, Tone.B1),

            Pair(R.id.btnC2, Tone.C2),
            Pair(R.id.btnC2s, Tone.C2s),
            Pair(R.id.btnD2, Tone.D2),
            Pair(R.id.btnD2s, Tone.D2s),
            Pair(R.id.btnE2, Tone.E2),
            Pair(R.id.btnF2, Tone.F2),
            Pair(R.id.btnF2s, Tone.F2s),
            Pair(R.id.btnG2, Tone.G2),
            Pair(R.id.btnG2s, Tone.G2s),
            Pair(R.id.btnA2, Tone.A2),
            Pair(R.id.btnA2s, Tone.A2s),
            Pair(R.id.btnB2, Tone.B2),

            Pair(R.id.btnC3, Tone.C3),
            Pair(R.id.btnC3s, Tone.C3s),
            Pair(R.id.btnD3, Tone.D3),
            Pair(R.id.btnD3s, Tone.D3s),
            Pair(R.id.btnE3, Tone.E3),
            Pair(R.id.btnF3, Tone.F3),
            Pair(R.id.btnF3s, Tone.F3s),
            Pair(R.id.btnG3, Tone.G3),
            Pair(R.id.btnG3s, Tone.G3s),
            Pair(R.id.btnA3, Tone.A3),
            Pair(R.id.btnA3s, Tone.A3s),
            Pair(R.id.btnB3, Tone.B3),
    )

    /**
     * ボタンのタップイベント
     * あまりよくない実装
     */
    @SuppressLint("ClickableViewAccessibility")
    private val btnTouchListener = View.OnTouchListener { v, event ->
        val osc = when(v.id){
            R.id.btnC1,R.id.btnC1s,R.id.btnD1,R.id.btnD1s,R.id.btnE1,R.id.btnF1,R.id.btnF1s,R.id.btnG1,R.id.btnG1s,R.id.btnA1, R.id.btnA1s,R.id.btnB1 -> findViewById<Spinner>(R.id.osc1Spinner).selectedItemPosition
            R.id.btnC2,R.id.btnC2s,R.id.btnD2,R.id.btnD2s,R.id.btnE2,R.id.btnF2,R.id.btnF2s,R.id.btnG2,R.id.btnG2s,R.id.btnA2, R.id.btnA2s,R.id.btnB2 -> findViewById<Spinner>(R.id.osc2Spinner).selectedItemPosition
            R.id.btnC3,R.id.btnC3s,R.id.btnD3,R.id.btnD3s,R.id.btnE3,R.id.btnF3,R.id.btnF3s,R.id.btnG3,R.id.btnG3s,R.id.btnA3, R.id.btnA3s,R.id.btnB3 -> findViewById<Spinner>(R.id.osc3Spinner).selectedItemPosition
            else -> 1
        }


        val level = when(v.id){
            R.id.btnC1,R.id.btnC1s,R.id.btnD1,R.id.btnD1s,R.id.btnE1,R.id.btnF1,R.id.btnF1s,R.id.btnG1,R.id.btnG1s,R.id.btnA1, R.id.btnA1s,R.id.btnB1 -> findViewById<SeekBar>(R.id.osc1Level).progress
            R.id.btnC2,R.id.btnC2s,R.id.btnD2,R.id.btnD2s,R.id.btnE2,R.id.btnF2,R.id.btnF2s,R.id.btnG2,R.id.btnG2s,R.id.btnA2, R.id.btnA2s,R.id.btnB2 -> findViewById<SeekBar>(R.id.osc2Level).progress
            R.id.btnC3,R.id.btnC3s,R.id.btnD3,R.id.btnD3s,R.id.btnE3,R.id.btnF3,R.id.btnF3s,R.id.btnG3,R.id.btnG3s,R.id.btnA3, R.id.btnA3s,R.id.btnB3 -> findViewById<SeekBar>(R.id.osc3Level).progress
            else -> 1
        }

        if(!keyMap.containsKey(v.id)){
            keyMap[v.id] = mutableListOf()
        }
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                if(toneMap.containsKey(v.id)){
                    var tone = toneMap[v.id]
                    keyMap[v.id]?.add(toneGenerator.toneOn(tone!!, level, osc))
                }
            }
            MotionEvent.ACTION_MOVE->{
            }
            MotionEvent.ACTION_UP->{
                keyMap[v.id]?.forEach {
                    toneGenerator.toneOff(it)
                }
            }
            MotionEvent.ACTION_CANCEL->{
                keyMap[v.id]?.forEach {
                    toneGenerator.toneOff(it)
                }
            }
            MotionEvent.ACTION_OUTSIDE->{
                keyMap[v.id]?.forEach {
                    toneGenerator.toneOff(it)
                }
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //オシレータの選択用のアダプタ
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        adapter.add("sin")
        adapter.add("Square 50%")
        adapter.add("Square 25%")
        adapter.add("Noise")
        findViewById<Spinner>(R.id.osc1Spinner).adapter = adapter
        findViewById<Spinner>(R.id.osc2Spinner).adapter = adapter
        findViewById<Spinner>(R.id.osc3Spinner).adapter = adapter

        // ボタンのIDの配列
        var ids = arrayListOf(
                R.id.btnC1,
                R.id.btnC1s,
                R.id.btnD1,
                R.id.btnD1s,
                R.id.btnE1,
                R.id.btnF1,
                R.id.btnF1s,
                R.id.btnG1,
                R.id.btnG1s,
                R.id.btnA1,
                R.id.btnA1s,
                R.id.btnB1,

                R.id.btnC2,
                R.id.btnC2s,
                R.id.btnD2,
                R.id.btnD2s,
                R.id.btnE2,
                R.id.btnF2,
                R.id.btnF2s,
                R.id.btnG2,
                R.id.btnG2s,
                R.id.btnA2,
                R.id.btnA2s,
                R.id.btnB2,

                R.id.btnC3,
                R.id.btnC3s,
                R.id.btnD3,
                R.id.btnD3s,
                R.id.btnE3,
                R.id.btnF3,
                R.id.btnF3s,
                R.id.btnG3,
                R.id.btnG3s,
                R.id.btnA3,
                R.id.btnA3s,
                R.id.btnB3,
        )

        // 各ボタンにイベント関連付け
        ids.forEach {
            var btn = findViewById<Button>(it)
            btn.setOnTouchListener(btnTouchListener)
        }

        //再生ボタンのイベント
        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            isPlay = true
            GlobalScope.launch(Dispatchers.Default ) {
                var trackMaxIdx = trackData.size - 1
                for(idx in 0..trackMaxIdx){
                    trackCntList[idx] = 0
                    curNoteList[idx] = null
                    startNote[idx] = 0L
                    keyMap[idx + 1000] = mutableListOf()
                }

                Log.i("tonegenerator", "BGM Play Start")
                while(isPlay){
                    for(idx in 0..trackMaxIdx) {
                        playTrack(idx)
                    }

                    var isEnd = true
                    for(idx in 0..trackMaxIdx){
                        if(trackCntList[idx] < trackData[idx].size){
                            isEnd = false
                        }
                    }
                    if(isEnd){
                        isPlay = false
                    }
                }

                for(idx in 0..trackMaxIdx) {
                    keyMap[idx + 1000]?.forEach {
                        toneGenerator.toneOff(it)
                    }
                }
                Log.i("tonegenerator", "BGM Play End")
            }
        }

        // 停止ボタンのイベント
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            isPlay = false
        }
    }

    /**
     * トラックの再生
     */
    private fun playTrack(trackNo : Int){
        val trackCnt = trackCntList[trackNo]
        var curNote = curNoteList[trackNo]
        val trackKey = trackNo + 1000
        val trackData = trackData[trackNo]

        if(startNote[trackNo] == 0L){
            startNote[trackNo] = Calendar.getInstance().timeInMillis
            if(trackData.size > trackCnt){
                curNoteList[trackNo] = trackData[trackCnt]
                curNote = curNoteList[trackNo]
                keyMap[trackKey]?.add(toneGenerator.toneOn(curNote!!.tone, 1, curNote.osc))
            }else{
                keyMap[trackKey]?.forEach {
                    toneGenerator.toneOff(it)
                }
            }
        }else{
            val diff = Calendar.getInstance().timeInMillis - startNote[trackNo]
            val len = (4.0 / curNote?.length!!) * step
            //Log.i("tonegenerator", "track = $trackNo : diff = $diff : len $len : step $step")
            if(diff >= len){
                keyMap[trackKey]?.forEach {
                    toneGenerator.toneOff(it)
                }
                trackCntList[trackNo]++;
                startNote[trackNo] = 0L
            }
        }
    }

}