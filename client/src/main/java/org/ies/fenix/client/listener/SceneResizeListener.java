package org.ies.fenix.client.listener;

/**
 * 3) How to use it
 * Where you create StageManager, pass an implementation:
 *
 * SceneResizeListener listener = newWidth -> {
 *     System.out.println("Scene resized to: " + newWidth);
 * };
 */
public interface SceneResizeListener {
    void onSceneResized(Number newWidth);
}