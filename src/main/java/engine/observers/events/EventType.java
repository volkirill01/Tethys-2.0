package engine.observers.events;

public enum EventType {
    Engine_StartPlay,
    Engine_StopPlay,
    Engine_Pause,
    Engine_Play,
    Engine_NextFrame,
    Engine_SaveScene,
    Engine_SaveSceneAs,
    Engine_OpenScene,
    Engine_ReloadScene,

    Engine_MousePositionCallback,
    Engine_MouseButtonCallback,
    Engine_MouseScrollCallback,

    Engine_KeyboardButtonCallback,

    Console_SendMessage
}
