//
//  SirenPlayer.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import AVFoundation

class SirenPlayer {
    weak var delegate: SirenPlayerDelegate?
    
    private var audioEngine: AVAudioEngine?
    private var playerNode: AVAudioPlayerNode?
    
    public var isActive: Bool {
        return playerNode?.isPlaying ?? false
    }
    public var isAvailable: Bool = false
    
    init() {
        checkSupport()
    }
    
    deinit {
        stopSiren()
    }
    
    private func checkSupport() {
        let audioSession = AVAudioSession.sharedInstance()
        
        var isAvailable = true
        do {
            try audioSession.setCategory(
                // needed to default to speaker
                .playAndRecord,
                mode: .default,
                options: [
                    // plays audio on phone speaker
                    // not connected bluetooth devices
                    .defaultToSpeaker,
                    // plays alongside others
                    .mixWithOthers,
                    // lowers volume of others
                    .duckOthers
                ]
            )
            
            // override the output audio port to speaker
            try audioSession.overrideOutputAudioPort(.speaker)
            
            // toggle audio session for testing
            try audioSession.setActive(true)
            try audioSession.setActive(false)
        } catch {
            print("SirenPlayer: error with audio session: \(error)")
            
            isAvailable = false
        }
        
        self.isAvailable = isAvailable
        delegate?.sirenAvailabilityUpdate(isAvailable)
    }
    
    func startSiren() {
        guard
            isAvailable
                && !isActive
        else { return }
        
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
        
        let buffer = createSirenBuffer(format: audioFormat)
        
        playerNode.scheduleBuffer(
            buffer,
            at: nil,
            options: .loops,
            completionHandler: nil
        )
        
        let audioSession = AVAudioSession.sharedInstance()
        do {
            // activate audio session
            try audioSession.setActive(true)
            
            // start audio engine
            try audioEngine.start()
            
            // play the siren buffer
            playerNode.play()
        } catch {
            print("SirenPlayer: error starting siren: \(error)")
            
            stopSiren()
            return
        }
        
        delegate?.sirenStarted()
    }
    
    func stopSiren() {
        // stop the siren buffer
        playerNode?.stop()
        playerNode = nil
        
        // stop audio engine
        audioEngine?.stop()
        audioEngine = nil
        
        let audioSession = AVAudioSession.sharedInstance()
        do {
            // deactivate audio session
            try audioSession.setActive(false)
        } catch {
            print("SirenPlayer: error stopping siren: \(error)")
            
            return
        }
        
        delegate?.sirenStopped()
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

protocol SirenPlayerDelegate: AnyObject {
    func sirenStarted()
    func sirenStopped()
    
    func sirenAvailabilityUpdate(_ isAvailable: Bool)
}
