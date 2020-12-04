package jndev.pseudo3d.sceneobject;

import jndev.pseudo3d.util.Vector;

/**
 * camera used to determine where to render a scene. entities in the same z position as the camera will be rendered 1:1
 * scale, entities behind the camera will be larger, and entities in front of the camera will be smaller
 *
 * @author JNDev (Jeremaster101)
 */
public class Camera {
    
    /**
     * field of view in radians, how wide the camera can see
     */
    private float fieldOfView;
    
    /**
     * how far away the camera is able to render
     */
    private float viewDistance;
    
    /**
     * camera sensor size. rendered scenes will scale to fit a square with side length of sensorSize pixels
     */
    private float sensorSize;
    
    /**
     * camera zoom, will scale rendered image by this amount
     */
    private float zoom;
    
    /**
     * rotation of the camera in radians, counter-clock-wise
     */
    private float rotation;
    
    /**
     * 3D position of camera in scene
     */
    private Vector scenePosition;
    
    /**
     * 2D position of camera in window
     */
    private Vector renderPosition;
    
    /**
     * creates a new camera centered at (0, 0, 0) with a fov of 100 degrees
     */
    public Camera() {
        scenePosition = new Vector();
        fieldOfView = 90;
        sensorSize = 500;
        viewDistance = 500;
        zoom = 1;
        rotation = 0;
        renderPosition = new Vector();
    }
    
    /**
     * constructs a new camera with set position, sensor res, and field of view
     *
     * @param scenePosition  camera position in scene
     * @param sensorSize     camera sensor size
     * @param fieldOfView    field of view in radians
     * @param viewDistance   z distance camera is able to see
     * @param zoom           camera zoom, scales render
     * @param rotation       rotation of camera in radians
     * @param renderPosition position of camera in window
     */
    public Camera(Vector scenePosition, Vector renderPosition, float sensorSize, float fieldOfView,
                  float viewDistance, float zoom, float rotation) {
        this.scenePosition = scenePosition;
        this.fieldOfView = fieldOfView;
        this.sensorSize = sensorSize;
        this.viewDistance = viewDistance;
        this.zoom = zoom;
        this.renderPosition = renderPosition;
        this.rotation = rotation;
    }
    
    /**
     * copies a camera to a new one
     *
     * @param camera camera to copy
     */
    public Camera(Camera camera) {
        scenePosition = camera.scenePosition;
        fieldOfView = camera.fieldOfView;
        sensorSize = camera.sensorSize;
        viewDistance = camera.viewDistance;
        zoom = camera.zoom;
        renderPosition = camera.renderPosition;
        rotation = camera.rotation;
    }
    
    /**
     * get field of view in radians
     *
     * @return field of view in radians
     */
    public float getFieldOfView() {
        return fieldOfView;
    }
    
    /**
     * set the camera's field of view
     *
     * @param fieldOfView field of view in radians
     */
    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }
    
    /**
     * get the position of the camera in the scene
     *
     * @return vector position of camera in scene
     */
    public Vector getScenePosition() {
        return scenePosition;
    }
    
    /**
     * set the camera's position in thew scene
     *
     * @param scenePosition new position in scene
     */
    public void setScenePosition(Vector scenePosition) {
        this.scenePosition = scenePosition;
    }
    
    /**
     * get position of camera in the render
     *
     * @return vector render position of camera
     */
    public Vector getRenderPosition() {
        return renderPosition;
    }
    
    /**
     * set the position of the camera in the render. set to null to use the center of the render bounds
     *
     * @param renderPosition 2D position vector
     */
    public void setRenderPosition(Vector renderPosition) {
        this.renderPosition = renderPosition;
    }
    
    /**
     * get sensor size of camera in pixels
     *
     * @return sensor size
     */
    public float getSensorSize() {
        return sensorSize;
    }
    
    /**
     * set the camera sensor size in pixels
     *
     * @param sensorSize new sensor size
     */
    public void setSensorSize(float sensorSize) {
        this.sensorSize = sensorSize;
    }
    
    /**
     * get the view distance for the camera
     *
     * @return camera view distance
     */
    public float getViewDistance() {
        return viewDistance;
    }
    
    /**
     * set how far away the camera is able to view entities
     *
     * @param viewDistance new view distance for camera
     */
    public void setViewDistance(float viewDistance) {
        this.viewDistance = viewDistance;
    }
    
    /**
     * get the zoom for the camera
     *
     * @return camera zoom
     */
    public float getZoom() {
        return zoom;
    }
    
    /**
     * set the zoom for the camera
     *
     * @param zoom camera zoom
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    
    /**
     * get the rotation of the camera
     *
     * @return rotation of camera in radians counter-clock-wise
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * set the rotation of the camera
     *
     * @param rotation rotation of camera in radians counter-clock-wise
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    /**
     * check if a camera is equal to this one
     *
     * @param o object to check
     * @return true if the object is equal to this camera
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camera that = (Camera) o;
        return Float.compare(that.fieldOfView, fieldOfView) == 0 &&
                Float.compare(that.viewDistance, viewDistance) == 0 &&
                Float.compare(that.sensorSize, sensorSize) == 0 &&
                scenePosition.equals(that.scenePosition);
    }
}
