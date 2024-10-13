//
//  SirenPlayer.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import AVFoundation

class SirenPlayer: ObservableObject {
    private var audioEngine: AVAudioEngine?
    private var playerNode: AVAudioPlayerNode?
    
    @Published public private(set) var isPlaying = false
    
    func startSiren() {
        // stop any existing siren
        stopSiren()
        
        audioEngine = AVAudioEngine()
        playerNode = AVAudioPlayerNode()
        
        guard
            let audioEngine = audioEngine,
            let playerNode = playerNode
        else { return }
        
        audioEngine.attach(playerNode)
        
        let audioFormat = AVAudioFormat(
            standardFormatWithSampleRate: 44100,
            channels: 1
        )!
        
        let mainMixer = audioEngine.mainMixerNode
        
        audioEngine.connect(
            playerNode,
            to: mainMixer,
            format: audioFormat
        )
        
        // configure audio session for background playback
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(
                .playAndRecord,
                mode: .default,
                options: [
                    .defaultToSpeaker,
                    .mixWithOthers,
                    .duckOthers
                ]
            )
            
            try audioSession.overrideOutputAudioPort(.speaker)
            
            try audioSession.setActive(true)
        } catch {
            print("SirenPlayer: error configuring audio session: \(error)")
            return
        }
        
        let buffer = createSirenBuffer(format: audioFormat)
        
        playerNode.scheduleBuffer(
            buffer,
            at: nil,
            options: .loops,
            completionHandler: nil
        )
        
        do {
            try audioEngine.start()
            
            playerNode.play()
        } catch {
            print("SirenPlayer: error starting audio engine: \(error)")
            return
        }
        
        DispatchQueue.main.async {
            self.isPlaying = true
        }
        
        print("SirenPlayer: started siren")
    }
    
    func stopSiren() {
        playerNode?.stop()
        audioEngine?.stop()
        
        audioEngine = nil
        playerNode = nil
        
        // deactivate audio session to conserve battery
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setActive(false)
        } catch {
            print("SirenPlayer: error deactivating audio session: \(error)")
            return
        }
        
        DispatchQueue.main.async {
            self.isPlaying = false
        }
        
        print("SirenPlayer: stopped siren")
    }
    
    // creates the siren sound
    private func createSirenBuffer(format: AVAudioFormat) -> AVAudioPCMBuffer {
        // duration of one siren cycle in seconds
        let duration: Double = 2.0
        
        let sampleRate = format.sampleRate
        let totalSamples = Int(sampleRate * duration)
        
        let buffer = AVAudioPCMBuffer(
            pcmFormat: format,
            frameCapacity: AVAudioFrameCount(totalSamples)
        )!
        
        buffer.frameLength = AVAudioFrameCount(totalSamples)
        
        let channels = buffer.floatChannelData!
        let channel = channels[0]
        
        let frequencyStart: Float = 600.0 // starting frequency in Hz
        let frequencyEnd: Float = 1200.0  // ending frequency in Hz
        let amplitude: Float = 1.0        // volume
        
        for sampleIndex in 0..<totalSamples {
            let t = Float(sampleIndex) / Float(sampleRate)
            
            // modulate frequency to create siren effect
            // goes from 0 to 1 to 0 over duration
            let modulation = sin(Float.pi * t / Float(duration))
            
            let frequency = frequencyStart + modulation * (frequencyEnd - frequencyStart)
            let sample = sin(2.0 * Float.pi * frequency * t) * amplitude
            
            channel[sampleIndex] = sample
        }
        
        return buffer
    }
}
