package jp.krohigewagma.tonegenerator.v2

import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.floor

/**
 *
 */
class ToneGenerator() {

    companion object{

        /**
         * AudioTrack
         */
        private var audioTrack : AudioTrack? = null

        /**
         * チャンネル
         */
        private val channels = arrayListOf<Channel>()

        init {
            channels.add(Channel())
            channels.add(Channel())
            channels.add(Channel())
            channels.add(Channel())
        }

        /**
         * 開始
         */
        fun start(){
            if(!ToneGeneratorConfig.isInitialize){
                Log.w(ToneGeneratorConfig.APP_NAME, "ToneGeneratorConfig not initialize.")
                return
            }

            if(null == audioTrack){
                audioTrack = ToneGeneratorConfig.createAudioTrack()
            }

            try{
                if(AudioTrack.PLAYSTATE_PLAYING != audioTrack?.playState){
                    Log.i(ToneGeneratorConfig.APP_NAME, "Play Start")
                    audioTrack?.play()
                }

                GlobalScope.launch(Dispatchers.Default) {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
                    while(AudioTrack.PLAYSTATE_PLAYING == audioTrack?.playState){
                        playTone()
                    }
                    Log.i(ToneGeneratorConfig.APP_NAME, "Play End")
                    audioTrack?.release()
                    audioTrack = null
                }
            }catch(e:Exception){
                Log.e(ToneGeneratorConfig.APP_NAME, e.message!!)
            }
        }

        /**
         * 音の再生
         */
        private fun playTone(){
            if(null == audioTrack
                    || AudioTrack.STATE_UNINITIALIZED == audioTrack?.state
                    || AudioTrack.PLAYSTATE_STOPPED == audioTrack?.playState
            ){
                return
            }

            try {
                if (AudioTrack.PLAYSTATE_PLAYING == audioTrack?.playState) {
/*
                    var mix = 0.0
                    channels.forEach {
                        mix += it.generator() / 32768.0f
                    }
                    mix *= 0.8
                    if(mix > 1.0f ) mix = 1.0
                    if(mix < -1.0f ) mix = -1.0
                    var outData = mix * 32768.0

                    var buff = ByteArray(1)
                    buff[0] = floor(outData).toInt().toByte()
 */
                    var buff = ByteArray(1)
                    var toneData = OSCManager.generate()
                    if(toneData > 0){
                        buff[0] = toneData
                        Log.i(ToneGeneratorConfig.APP_NAME, "buff : $toneData")
                        if(buff.isNotEmpty()){
                            Log.i(ToneGeneratorConfig.APP_NAME, "write buffer")
                            when(audioTrack?.write(buff, 0, buff.size)){
                                AudioTrack.ERROR_BAD_VALUE -> Log.i(ToneGeneratorConfig.APP_NAME, "ERROR_BAD_VALUE")
                                AudioTrack.ERROR_DEAD_OBJECT -> Log.i(ToneGeneratorConfig.APP_NAME, "ERROR_DEAD_OBJECT")
                                AudioTrack.ERROR_INVALID_OPERATION -> Log.i(ToneGeneratorConfig.APP_NAME, "ERROR_INVALID_OPERATION")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(ToneGeneratorConfig.APP_NAME, "Error!!!", e)
            }
        }

        /**
         * 停止
         */
        fun stop(){
            if(!ToneGeneratorConfig.isInitialize || null == audioTrack){
                Log.w(ToneGeneratorConfig.APP_NAME, "ToneGeneratorConfig not initialize.")
                return
            }
            audioTrack?.stop()
        }

        /**
         * 音の設定
         * @param channel
         * @param tone
         * @param level
         */
        fun toneOn(channel:Int, tone : Tone, level : Int){
            if(!ToneGeneratorConfig.isInitialize || null == audioTrack){
                Log.w(ToneGeneratorConfig.APP_NAME, "ToneGeneratorConfig not initialize.")
                return
            }
            channels[channel].toneOn(tone, level)
        }

        /**
         * 音の停止
         * @param channel
         */
        fun toneOff(channel:Int, tone : Tone){
            if(!ToneGeneratorConfig.isInitialize || null == audioTrack){
                Log.w(ToneGeneratorConfig.APP_NAME, "ToneGeneratorConfig not initialize.")
                return
            }
            channels[channel].toneOff(tone)
        }

    }



}