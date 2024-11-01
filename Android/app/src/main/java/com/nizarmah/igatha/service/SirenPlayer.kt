package com.nizarmah.igatha.service

import android.content.Context
import android.media.*
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.sin

class SirenPlayer(context: Context) {
    // StateFlows for isActive and isAvailable
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        // assume supported
        _isAvailable.value = true
    }

    fun deinit() {
        stopSiren()
    }

    fun startSiren() {
        if (!_isAvailable.value || _isActive.value) return

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val sampleRate = 44100
            val buffer = createSirenBuffer()

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(buffer.size * 2)
                .build()

            // Route audio to built-in speaker
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager.isSpeakerphoneOn = true

            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val speakerDevice = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
            if (speakerDevice != null) {
                audioTrack?.preferredDevice = speakerDevice
            } else {
                Log.e(TAG, "Speaker device not found")
            }

            audioTrack?.let { track ->
                track.write(buffer, 0, buffer.size)
                track.setLoopPoints(0, buffer.size / track.channelCount, -1)
                track.play()
                _isActive.value = true
                Log.d(TAG, "Siren started")
            } ?: run {
                _isAvailable.value = false
                Log.e(TAG, "Failed to create AudioTrack")
            }
        } catch (e: Exception) {
            _isAvailable.value = false
            Log.e(TAG, "Exception in startSiren: ${e.message}", e)
        }
    }

    fun stopSiren() {
        try {
            // Reset audio mode and routing
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = false

            audioTrack?.let { track ->
                track.stop()
                track.release()
                Log.d(TAG, "Siren stopped")
            }
            audioTrack = null
            _isActive.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Exception in stopSiren: ${e.message}", e)
        }
    }

    private fun createSirenBuffer(): ShortArray {
        return try {
            val duration = 2.0 // seconds
            val sampleRate = 44100 // Hz
            val totalSamples = (sampleRate * duration).toInt()
            val buffer = ShortArray(totalSamples)

            val frequencyStart = 600.0f // Hz
            val frequencyEnd = 1200.0f // Hz
            val amplitude = 0.8f // Volume (0.0 to 1.0)

            for (sampleIndex in 0 until totalSamples) {
                val t = sampleIndex / sampleRate.toFloat()

                // Modulate frequency to create siren effect
                val modulation = sin(PI.toFloat() * t / duration.toFloat())

                val frequency = frequencyStart + modulation * (frequencyEnd - frequencyStart)
                val sample = sin(2.0f * PI.toFloat() * frequency * t) * amplitude

                // Convert to 16-bit PCM value
                val pcmValue = (sample * Short.MAX_VALUE).toInt().toShort()
                buffer[sampleIndex] = pcmValue
            }

            buffer
        } catch (e: Exception) {
            Log.e(TAG, "Exception in createSirenBuffer: ${e.message}", e)
            _isAvailable.value = false
            ShortArray(0)
        }
    }

    companion object {
        private const val TAG = "SirenPlayer"
    }
}
