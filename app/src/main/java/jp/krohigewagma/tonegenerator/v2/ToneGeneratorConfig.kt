package jp.krohigewagma.tonegenerator.v2

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log

/**
 * ToneGenrator設定
 */
class ToneGeneratorConfig {
    companion object{

        /**
         * ログで使用するアプリ芽生
         */
        const val APP_NAME = "ToneGenerator"

        /**
         * サンプリング周波数
         */
        var sampleRate : Int = 0
            private set

        /**
         * チャンネル数(現在未使用)
         */
        var channel : Int = 0
            private set

        /**
         * ビットレート
         */
        var bitRate : Int = 0
            private set

        /**
         * 初期化フラグ
         * true:初期化済み
         * false:実初期化
         */
        var isInitialize = false
            private set

        /**
         * オーディオトラック
         */
        lateinit var audioTrack : AudioTrack
            private set

        /**
         * 初期化
         * @return 初期化できた場合はtrue
         */
        fun initialize(sampleRate : Int, channel : Int, bitRate : Int) : Boolean{
            if(isInitialize){
                // 初期化済みの場合は設定を変更できない
                return false
            }
            Companion.sampleRate = sampleRate
            Companion.channel = channel
            Companion.bitRate = bitRate
            isInitialize = true

            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            return true
        }

        /**
         * AudiopTrackオブジェクトの生成
         */
        fun createAudioTrack() : AudioTrack{

            //var minBuffSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT)
            var audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                            AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                    )
                    .setAudioFormat(
                            AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                    .setSampleRate(sampleRate)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    //.setBufferSizeInBytes(minBuffSize * 2)
                    .build()

            audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener{
                override fun onMarkerReached(track: AudioTrack?) {
                    Log.d(APP_NAME, "onMarkerReached")
                }

                override fun onPeriodicNotification(track: AudioTrack?) {
                    if(AudioTrack.PLAYSTATE_STOPPED == track?.playState){
                        Log.d(APP_NAME, "onPeriodicNotification End")
                    }
                }
            })

            return audioTrack
        }
    }
}