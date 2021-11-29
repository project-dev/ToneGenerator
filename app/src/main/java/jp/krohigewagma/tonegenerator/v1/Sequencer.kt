package jp.krohigewagma.tonegenerator.v1

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 音楽再生を制御する
 */
class Sequencer {


    companion object{
        /**
         * 状態
         */
        var status = STATUS.STOP
            private set

        /**
         * 現在のtick数
         */
        var tickCount = 0
            private set

        /**
         * テンポ
         */
        private var tempo = 120

        /**
         * 分解能
         */
        private var tick = 480

        /**
         * 1ティック当たりの時間
         */
        var tickOfTime = 0.0
            private set

        /**
         * ToneController
         */
        private lateinit var toneCtrl : ToneController

        /**
         * トラック
         */
        private var trackList = mutableListOf<Track>()

        /**
         * 初期化
         */
        fun initialize(tg : ToneController, tempo : Int, tick : Int){
            this.toneCtrl = tg
            this.tempo = tempo
            this.tick = tick
            updateTickOfTime()
            this.trackList.add(Track(1))
            this.trackList.add(Track(2))
            this.trackList.add(Track(3))
            this.trackList.add(Track(4))
        }

        /**
         *
         */
        public final fun getToneController() : ToneController{
            return this.toneCtrl
        }

        /**
         * 分解能をセット
         */
        fun setTick(tick : Int){
            this.tick = tick
            updateTickOfTime()
        }

        /**
         * 分解能を取得
         */
        fun getTick() : Int{
            return this.tick
        }

        /**
         * テンポをセット
         */
        fun setTempo(tempo : Int){
            this.tempo = tempo
            updateTickOfTime()
        }

        /**
         * テンポを取得
         */
        fun getTempo() : Int{
            return this.tempo
        }

        /**
         * 1Tick当たりの自時間を求める
         */
        private fun updateTickOfTime(){
            this.tickOfTime = (60.0 * 1000.0 / tempo / tick)
            //Log.i(ToneController.APP_NAME, "tickofTime $tickOfTime")
        }

        /**
         * 再生
         */
        fun play(noteData : List<List<Note>>){
            if(this.status != STATUS.STOP){
                return
            }

            this.status = STATUS.READY

            /**
             * ノートをセットして、トラックを開始する
             */
            val max = if ( noteData.size > 4 ) 3 else noteData.size - 1
            tickCount = 0
            for(i in 0..max){
                this.trackList[i].setNote(noteData[i])
                this.trackList[i].start()
            }

            GlobalScope.launch(Dispatchers.Default ) {
                Sequencer.status = STATUS.PLAY
                var startTime = System.nanoTime()
                while (Sequencer.status == STATUS.PLAY ){
                    // 厳密に処理したいのでナノ秒で見る
                    var checkTime = System.nanoTime();
                    if(checkTime - startTime > tickOfTime * 1000 * 1000){
                        startTime = checkTime
                        tickCount++
                    }
                    var isEnd = true
                    for(i in 0..max){
                        if(trackList[i].isPlay){
                            isEnd = false
                        }
                    }
                    if(isEnd){
                        stop()
                    }
                }

            }
        }

        /**
         * ノートの長さをtick数に変換する
         */
        fun noteToTick(note : Note) : Long{
            /**
             * MEMO:分解能(四分音符を表す数値)
             * 全音符         480 * 4 = 1920
             * 4分音符        480 * 2 =  960
             * 4分音符  三連符 960 / 3 =  320
             * 4分音符        480
             * 4分音符　三連符 480 / 3 =  160
             * 8分音符        480 / 2 =  240
             * 8分音符  三連符 240 / 3 =   80
             * 16分音符       480 / 4 =  120
             * 16分音符 三連符 120 / 3 =   40
             * 32分音符       480 / 8 =   60
             * 32分音符 三連符  60 / 3 =   20
             */
            if(note.length == 0){
                return 0;
            }
            return (this.tick * (4.0 / note.length)).toLong();
        }

        /**
         * 停止
         */
        fun stop(){
            if(this.status != STATUS.PLAY){
                return
            }

            /**
             * トラックを停止する
             */
            trackList.forEach {
                it.end()
            }
            this.toneCtrl.reset()
            this.status = STATUS.STOP
        }

    }

    /**
     * 状態
     */
    public enum class STATUS{
        /**
         * 再生を停止している
         */
        STOP,

        /**
         * 再生を支持され、再生されるのを待っている
         */
        READY,

        /**
         * 再生
         */
        PLAY
    }

}
