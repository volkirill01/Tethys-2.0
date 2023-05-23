package engine.audio;

import engine.profiling.Profiler;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {

    private final int bufferID;
    private final int sourceID;  // TODO MOVE THIS IN SEPARATE COMPONENT
    private final String filepath;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops) {
        Profiler.startTimer(String.format("Load Sound - '%s'", filepath));
        this.filepath = filepath;

        // Allocate space to store the return information from stb
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        Profiler.startTimer("Load Sound From File");
        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
        Profiler.stopTimer("Load Sound From File");
        if (rawAudioBuffer == null) {
            stackPop();
            stackPop();
            throw new RuntimeException(String.format("Could not load sound - '%s'", filepath));
        }

        // Retrieve the extra information that was stored in the buffers by stb
        int channels = channelsBuffer.get(0);
        int sampleRate = sampleRateBuffer.get(0);
        // Free memory
        stackPop();
        stackPop();

        // Find the correct openAL format
        int format;

        switch (channels) {
            case 1 -> format = AL_FORMAT_MONO16;
            case 2 -> format = AL_FORMAT_STEREO16;
            default -> throw new IllegalStateException(String.format("Sound contains unknown number of channels - '%s'", filepath));
        }

        this.bufferID = alGenBuffers();
        alBufferData(this.bufferID, format, rawAudioBuffer, sampleRate);

        // Generate the source  // TODO MOVE THIS IN SEPARATE COMPONENT
        this.sourceID = alGenSources();

        alSourcei(this.sourceID, AL_BUFFER, this.bufferID); // TODO MAKE IT CHANGEABLE FROM COMPONENT
        alSourcei(this.sourceID, AL_LOOPING, loops ? AL_TRUE : AL_FALSE); // TODO MAKE IT CHANGEABLE FROM COMPONENT
        alSourcei(this.sourceID, AL_POSITION, 0);
        alSourcef(this.sourceID, AL_GAIN, 0.3f); // TODO MAKE IT CHANGEABLE FROM COMPONENT

        // Free stb raw audio buffer
        free(rawAudioBuffer);
        Profiler.stopTimer(String.format("Load Sound - '%s'", filepath));
    }

    public void freeMemory() {
        alDeleteSources(this.sourceID);  // TODO MOVE THIS IN SEPARATE COMPONENT
        alDeleteBuffers(this.bufferID);
    }

    public void play() {
        int state = alGetSourcei(this.sourceID, AL_SOURCE_STATE); // TODO MAKE IT CHANGEABLE FROM COMPONENT
        if (state == AL_STOPPED) {
            this.isPlaying = false;
            alSourcei(this.sourceID, AL_POSITION, 0);
        }

        if (!this.isPlaying) {
            alSourcePlay(this.sourceID);
            this.isPlaying = true;
        }
    }

    public void stop() {
        if (this.isPlaying) {
            alSourceStop(this.sourceID);
            this.isPlaying = false;
        }
    }

    public String getFilepath() { return this.filepath; }

    public boolean isPlaying() {
        int state = alGetSourcei(this.sourceID, AL_SOURCE_STATE);
        if (state == AL_STOPPED)
            this.isPlaying = false;

        return this.isPlaying;
    }
}
