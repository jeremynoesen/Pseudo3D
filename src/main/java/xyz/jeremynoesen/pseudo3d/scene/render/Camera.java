package xyz.jeremynoesen.pseudo3d.scene.render;

import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.Objects;

/**
 * camera used to determine where to render a scene
 *
 * @author Jeremy Noesen
 */
public class Camera {
    
    /**
     * field of view in degrees, how wide the camera can see
     */
    private float fieldOfView;
    
    /**
     * how far away the camera is able to render in grid units
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
     * rotation of the camera in degrees, counter-clock-wise
     */
    private float rotation;
    
    /**
     * 3D position of camera in scene in grid units
     */
    private Vector position;
    
    /**
     * 2D position of camera in window in pixels
     */
    private Vector offset;
    
    /**
     * creates a new default camera
     */
    public Camera() {
        position = new Vector();
        fieldOfView = 90;
        sensorSize = 500;
        viewDistance = 32;
        zoom = 1;
        rotation = 0;
        offset = new Vector();
    }
    
    /**
     * constructs a new camera with set position, sensor res, and field of view
     *
     * @param position     camera position in scene
     * @param sensorSize   camera sensor size
     * @param fieldOfView  field of view in degrees
     * @param viewDistance z distance camera is able to see in grid units
     * @param zoom         camera zoom, scales render
     * @param rotation     rotation of camera in degrees
     * @param offset       position of camera in window
     */
    public Camera(Vector position, Vector offset, float sensorSize, float fieldOfView,
                  float viewDistance, float zoom, float rotation) {
        this.position = position;
        this.fieldOfView = fieldOfView;
        this.sensorSize = sensorSize;
        this.viewDistance = viewDistance;
        this.zoom = zoom;
        this.offset = offset;
        this.rotation = rotation;
    }
    
    /**
     * copies a camera to a new one
     *
     * @param camera camera to copy
     */
    public Camera(Camera camera) {
        position = camera.position;
        fieldOfView = camera.fieldOfView;
        sensorSize = camera.sensorSize;
        viewDistance = camera.viewDistance;
        zoom = camera.zoom;
        offset = camera.offset;
        rotation = camera.rotation;
    }
    
    /**
     * get field of view in degrees
     *
     * @return field of view in degrees
     */
    public float getFieldOfView() {
        return fieldOfView;
    }
    
    /**
     * set the camera's field of view
     *
     * @param fieldOfView field of view in degrees
     */
    public Camera setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
        return this;
    }
    
    /**
     * get the position of the camera in the scene
     *
     * @return vector position of camera in scene
     */
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the camera's position in thew scene
     *
     * @param position new position in scene
     */
    public Camera setPosition(Vector position) {
        this.position = position;
        return this;
    }
    
    /**
     * get offset of render center
     *
     * @return vector render offset of camera
     */
    public Vector getOffset() {
        return offset;
    }
    
    /**
     * set the offset from the center of the camera in the render, in pixels
     *
     * @param offset 2D position vector
     */
    public Camera setOffset(Vector offset) {
        this.offset = offset;
        return this;
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
    public Camera setSensorSize(float sensorSize) {
        this.sensorSize = sensorSize;
        return this;
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
    public Camera setViewDistance(float viewDistance) {
        this.viewDistance = viewDistance;
        return this;
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
    public Camera setZoom(float zoom) {
        this.zoom = zoom;
        return this;
    }
    
    /**
     * get the rotation of the camera
     *
     * @return rotation of camera in degrees counter-clock-wise
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * set the rotation of the camera
     *
     * @param rotation rotation of camera in degrees counter-clock-wise
     */
    public Camera setRotation(float rotation) {
        this.rotation = rotation;
        return this;
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
        return Float.compare(camera.fieldOfView, fieldOfView) == 0 &&
                Float.compare(camera.viewDistance, viewDistance) == 0 &&
                Float.compare(camera.sensorSize, sensorSize) == 0 &&
                Float.compare(camera.zoom, zoom) == 0 &&
                Float.compare(camera.rotation, rotation) == 0 &&
                Objects.equals(position, camera.position) &&
                Objects.equals(offset, camera.offset);
    }
}
