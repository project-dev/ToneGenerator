package jp.krohigewagma.tonegenerator.v1_1

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * トラックの管理
 */
class Track(var trackNo : Int) {

    /**
     * 音符データ
     */
    private var noteList = mutableListOf<Note>()

    /**
     * 音符データをセットする
     */
    fun setNote(noteList : List<Note>){
        this.noteList.clear()
        this.noteList.addAll(noteList)
    }

    /**
     * 音を鳴らし始めたときのTick数
     */
    private var prevTickCnt = 0

    /**
     * Tone on状態かどうか
     */
    private var isToneOn = false

    /**
     * ノートのカウント
     */
    private var noteCnt =0

    /**
     * 嗄声状態フラグ(これ、名前変えたい)
     */
    var isPlay = false

    /**
     * 開始する
     */
    fun start(){
        if(isPlay){
            return
        }

        isPlay = true

        GlobalScope.launch(Dispatchers.Default ) {
            prevTickCnt = 0
            isToneOn = false
            noteCnt =0
            var toneId = 0
            while(isPlay){
                if(noteCnt >= noteList.size || Sequencer.status == Sequencer.STATUS.STOP){
                    isPlay = false
                    continue
                }else if(Sequencer.status == Sequencer.STATUS.READY){
                    continue
                }

                if(!isToneOn){
                    isToneOn = true
                    prevTickCnt = Sequencer.tickCount
                    var note = noteList[noteCnt]
                    toneId = ToneGenerator.toneOn(note.tone, 1.0f, note.osc)

                }else if(isToneOn && Sequencer.tickCount - prevTickCnt >= (Sequencer.noteToTick(noteList[noteCnt]) - 1) ){
                    isToneOn = false
                    ToneGenerator.toneOff(toneId)
                    toneId = 0
                    noteCnt++
                }
            }
            if(toneId != 0){
                ToneGenerator.toneOff(toneId)
            }
        }
    }

    /**
     * 終了する
     */
    fun end(){
        isToneOn = false
        isPlay = false
    }

}