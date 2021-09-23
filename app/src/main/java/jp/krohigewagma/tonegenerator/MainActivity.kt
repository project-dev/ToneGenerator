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
import java.util.*

class MainActivity : AppCompatActivity(){

    private val toneGenerator = ToneController(22050, 1, 8)

    var keyMap = mutableMapOf<Int, MutableList<Int>>()

    var track1Data = listOf<Note>(
            Note(Tone.E3, 8, 0),
            Note(Tone.D3, 8, 0),
            Note(Tone.C3, 8, 0),
            Note(Tone.B2, 8, 0),
            Note(Tone.C3, 8, 0),
            Note(Tone.D3, 8, 0),
            Note(Tone.E3, 4, 0),
            Note(Tone.C3, 8, 0),
            Note(Tone.A2, 4, 0),
            Note(Tone.A2, 8, 0),

            Note(Tone.A3, 8, 0),
            Note(Tone.G3, 8, 0),
            Note(Tone.F3, 8, 0),
            Note(Tone.D3, 8, 0),
            Note(Tone.E3, 8, 0),
            Note(Tone.F3, 8, 0),
            Note(Tone.E3, 2, 0),
    )
    var track2Data = listOf<Note>(
            Note(Tone.NONE, 8, 2),
            Note(Tone.C2, 8, 2),
            Note(Tone.E2, 8, 2),

            Note(Tone.NONE, 8, 2),
            Note(Tone.C2, 8, 2),
            Note(Tone.E2, 8, 2),

            Note(Tone.NONE, 8, 2),
            Note(Tone.C2, 8, 2),
            Note(Tone.E2, 8, 2),

            Note(Tone.NONE, 8, 2),
            Note(Tone.C2, 8, 2),
            Note(Tone.E2, 8, 2),

            Note(Tone.NONE, 8, 2),
            Note(Tone.A1, 8, 2),
            Note(Tone.C2, 8, 2),

            Note(Tone.NONE, 8, 2),
            Note(Tone.B1, 8, 2),
            Note(Tone.D2, 8, 2),

            Note(Tone.NONE, 8, 2),
            Note(Tone.E2, 8, 2),
            Note(Tone.D2, 8, 2),

            Note(Tone.C2, 8, 2),
            Note(Tone.D2, 8, 2),
            Note(Tone.B1, 8, 2),
    )

    var track1Cnt = 0
    var track2Cnt = 0
    var isPlay = false

    var step = 60 * 1000 / 120

    private var bgmThrtead : Thread? = null

    var toneMap = mapOf(
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
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        adapter.add("sin")
        adapter.add("Square 50%")
        adapter.add("Square 25%")
        adapter.add("Noise")
        findViewById<Spinner>(R.id.osc1Spinner).adapter = adapter
        findViewById<Spinner>(R.id.osc2Spinner).adapter = adapter
        findViewById<Spinner>(R.id.osc3Spinner).adapter = adapter

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

        ids.forEach {
            var btn = findViewById<Button>(it)
            btn.setOnTouchListener(btnTouchListener)
        }


        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            track1Cnt = 0
            track2Cnt = 0
            isPlay = true

            bgmThrtead = Thread(Runnable {
                var curNote1 : Note? = null
                var startNot1 = 0L
                var curNote2 : Note? = null
                var startNot2 = 0L
                keyMap[1001] = mutableListOf()
                keyMap[1002] = mutableListOf()

                Log.i("tonegenerator", "BGM Play Start")
                while(isPlay){

                    if(startNot1 == 0L){
                        startNot1 = Calendar.getInstance().timeInMillis
                        if(track1Data.size > track1Cnt){
                            curNote1 = track1Data[track1Cnt]
                            keyMap[1001]?.add(toneGenerator.toneOn(curNote1.tone, 1, curNote1.osc))
                        }else{
                            keyMap[1001]?.forEach {
                                toneGenerator.toneOff(it)
                            }
                        }
                    }else{
                        var diff = Calendar.getInstance().timeInMillis - startNot1
                        var len = (4.0 / curNote1?.length!!) * step
                        //Log.i("tonegenerator", "diff = $diff : len $len : step $step")
                        if(diff >= len){
                            keyMap[1001]?.forEach {
                                toneGenerator.toneOff(it)
                            }
                            track1Cnt++;
                            startNot1 = 0L
                        }
                    }

                    if(startNot2 == 0L){
                        startNot2 = Calendar.getInstance().timeInMillis
                        if(track2Data.size > track2Cnt){
                            curNote2 = track2Data[track2Cnt]
                            keyMap[1002]?.add(toneGenerator.toneOn(curNote2.tone, 1, curNote2.osc))
                        }else{
                            keyMap[1002]?.forEach {
                                toneGenerator.toneOff(it)
                            }
                        }
                    }else{
                        var diff = Calendar.getInstance().timeInMillis - startNot2
                        var len = (4.0 / curNote2?.length!!) * step
                        if(diff >= len){
                            keyMap[1002]?.forEach {
                                toneGenerator.toneOff(it)
                            }
                            track2Cnt++;
                            startNot2 = 0L
                        }
                    }


                    if(track1Cnt >= track1Data.size && track2Cnt >= track2Data.size){
                        isPlay = false
                    }
                }
                Log.i("tonegenerator", "BGM Play End")

            })
            bgmThrtead?.start()
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            isPlay = false
            bgmThrtead?.interrupt()
            bgmThrtead = null
        }

    }

}