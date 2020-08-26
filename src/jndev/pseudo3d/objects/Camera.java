package jndev.pseudo3d.objects;

import jndev.pseudo3d.utils.Vector;

/**
 * camera used to determine where to render a scene. objects in the same z position as the camera will be rendered 1:1
 * scale, objects behind the camera will be larger, and objects in front of the camera will be smaller
 *
 * @author JNDev (Jeremaster101)
 */
public class Camera {
    
    /**
     * field of view in degrees, how wide the camera can see
     */
    private double fieldOfView;
    
    /**
     * how far away the camera is able to render
     */
    private double viewDistance;
    
    /**
     * camera sensor size. rendered scenes will scale to fit a square with side length of sensorSize pixels
     */
    private double sensorSize;
    
    /**
     * camera zoom, will scale rendered image by this amount
     */
    private double zoom;
    
    /**
     * 3D position of camera in scene
     */
    private Vector scenePosition;
    
    /**
     * 2D position of camera in window
     */
    private Vector windowPosition;
    
    /**
     * creates a new camera centered at (0, 0, 0) with a fov of 100 degrees
     */
    public Camera() {
        scenePosition = new Vector(0, 0, 0);
        fieldOfView = 90;
        sensorSize = 500;
        viewDistance = 500;
        zoom = 1;
        windowPosition = null;
    }
    
    /**
     * constructs a new camera with set position, sensor res, and field of view
     *
     * @param scenePosition camera position in scene
     * @param sensorSize    camera sensor size
     * @param fieldOfView   field of view in degrees
     * @param viewDistance  z distance camera is able to see
     * @param zoom          camera zoom, scales render
     */
    public Camera(Vector scenePosition, Vector windowPosition, double sensorSize, double fieldOfView, double viewDistance, double zoom) {
        this.scenePosition = scenePosition;
        this.fieldOfView = fieldOfView;
        this.sensorSize = sensorSize;
        this.viewDistance = viewDistance;
        this.zoom = zoom;
        this.windowPosition = windowPosition;
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
        windowPosition = camera.windowPosition;
    }
    
    /**
     * get field of view in degrees
     *
     * @return field of view in degrees
     */
    public double getFieldOfView() {
        return fieldOfView;
    }
    
    /**
     * set the camera's field of view
     *
     * @param fieldOfView field of view in degrees
     */
    public void setFieldOfView(double fieldOfView) {
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
     * get position of camera in window
     *
     * @return vector window position of camera
     */
    public Vector getWindowPosition() {
        return windowPosition;
    }
    
    /**
     * set the position of the camera in the window. set to null to use the center of the window
     *
     * @param windowPosition 2D position vector
     */
    public void setWindowPosition(Vector windowPosition) {
        this.windowPosition = windowPosition;
    }
    
    /**
     * get sensor size of camera in pixels
     *
     * @return sensor size
     */
    public double getSensorSize() {
        return sensorSize;
    }
    
    /**
     * set the camera sensor size in pixels
     *
     * @param sensorSize new sensor size
     */
    public void setSensorSize(double sensorSize) {
        this.sensorSize = sensorSize;
    }
    
    /**
     * get the view distance for the camera
     *
     * @return camera view distance
     */
    public double getViewDistance() {
        return viewDistance;
    }
    
    /**
     * set how far away the camera is able to view objects
     *
     * @param viewDistance new view distance for camera
     */
    public void setViewDistance(double viewDistance) {
        this.viewDistance = viewDistance;
    }
    
    /**
     * get the zoom for the camera
     *
     * @return camera zoom
     */
    public double getZoom() {
        return zoom;
    }
    
    /**
     * set the zoom for the camera
     *
     * @param zoom camera zoom
     */
    public void setZoom(double zoom) {
        this.zoom = zoom;
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
        Camera camera = (Camera) o;
        return Double.compare(camera.fieldOfView, fieldOfView) == 0 &&
                Double.compare(camera.viewDistance, viewDistance) == 0 &&
                Double.compare(camera.sensorSize, sensorSize) == 0 &&
                scenePosition.equals(camera.scenePosition);
    }
}