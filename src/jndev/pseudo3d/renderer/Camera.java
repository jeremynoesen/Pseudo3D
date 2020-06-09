package jndev.pseudo3d.renderer;

import jndev.pseudo3d.util.Vector;

/**
 * this is used to determine where to render a scene
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
     * position of camera
     */
    private Vector position;
    
    /**
     * creates a new camera centered at (0, 0, 0) with a fov of 100 degrees
     */
    public Camera() {
        position = new Vector(0, 0, 0);
        fieldOfView = 90;
        sensorSize = 500;
        viewDistance = 500;
    }
    
    /**
     * constructs a new camera with set position, sensor res, and field of view
     *
     * @param position camera position
     * @param fieldOfView field of view in degrees
     */
    public Camera(Vector position, double sensorSize, double fieldOfView, double viewDistance) {
        this.position = position;
        this.fieldOfView = fieldOfView;
        this.sensorSize = sensorSize;
        this.viewDistance = viewDistance;
    }
    
    /**
     * copies a camera to a new one
     *
     * @param camera camera to copy
     */
    public Camera(Camera camera) {
        position = camera.getPosition();
        fieldOfView = camera.getFieldOfView();
        sensorSize = camera.getSensorSize();
        viewDistance = camera.getViewDistance();
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
     * get the position of the camera
     *
     * @return vector position of camera
     */
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the camera's position
     *
     * @param position new position
     */
    public void setPosition(Vector position) {
        this.position = position;
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
}
