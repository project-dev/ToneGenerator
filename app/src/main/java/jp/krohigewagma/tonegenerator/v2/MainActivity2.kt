package jp.krohigewagma.tonegenerator.v2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import jp.krohigewagma.tonegenerator.R

class MainActivity2 : AppCompatActivity(){

    /**
     * UIのボタンととOSCObjectのマッピング
     */
    private var keyMap = mutableMapOf<Int, MutableList<Int>>()

    /**
     * ボタンと音のマッピング
     */
    private var toneMap = mapOf(
            Pair(R.id.keyF0,  Tone.C3),
            Pair(R.id.keyFs0, Tone.C3s),
            Pair(R.id.keyG0,  Tone.D3),
            Pair(R.id.keyGs0, Tone.D3s),
            Pair(R.id.keyA0,  Tone.E3),
            Pair(R.id.keyB0,  Tone.F3),
            Pair(R.id.keyAs0, Tone.F3s),
            Pair(R.id.keyC1,  Tone.G3),
            Pair(R.id.keyCs1, Tone.G3s),
            Pair(R.id.keyD1,  Tone.A3),
            Pair(R.id.keyDs1, Tone.A3s),
            Pair(R.id.keyE1,  Tone.B3),
    )

    /**
     * ボタンのタップイベント
     * あまりよくない実装
     */
    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { v, event ->
        val osc = findViewById<Spinner>(R.id.oscSpinner).selectedItemPosition

        val level = findViewById<SeekBar>(R.id.oscLevel).progress

        if(!keyMap.containsKey(v.id)){
            keyMap[v.id] = mutableListOf()
        }

        if(toneMap.containsKey(v.id)) {
            var tone = toneMap[v.id]

            var ch = 0

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ToneGenerator.toneOn(ch, tone!!, level)
                }

                MotionEvent.ACTION_MOVE -> {
                }

                MotionEvent.ACTION_UP -> {
                    ToneGenerator.toneOff(ch, tone!!)
                }
                MotionEvent.ACTION_CANCEL -> {
                    ToneGenerator.toneOff(ch, tone!!)
                }
                MotionEvent.ACTION_OUTSIDE -> {
                    ToneGenerator.toneOff(ch, tone!!)
                }
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyborad1)

        /**
         * 設定の初期化
         */
        ToneGeneratorConfig.initialize(22050, 1, 8)
        ToneGenerator.start()

        //オシレータの選択用のアダプタ
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        adapter.add("sin")
        adapter.add("Square 50%")
        adapter.add("Square 25%")
        adapter.add("Noise")
        findViewById<Spinner>(R.id.oscSpinner).adapter = adapter

        // ボタンのIDの配列
        var ids = arrayListOf(
                R.id.keyF0,
                R.id.keyFs0,
                R.id.keyG0,
                R.id.keyGs0,
                R.id.keyA0,
                R.id.keyB0,
                R.id.keyAs0,
                R.id.keyC1,
                R.id.keyCs1,
                R.id.keyD1,
                R.id.keyDs1,
                R.id.keyE1,
        )

        // 各ボタンにイベント関連付け
        ids.forEach {
            var key = findViewById<ImageView>(it)
            key.setOnTouchListener(touchListener)
        }
    }
}