package com.example.cropimagedemo.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * ImageView able to be zoomed by pinch motion. Zoomed in bitmap can also be
 * moved around container. The current zoom scale and center point of the
 * container is kept over different bitmaps.
 * <p/>
 * By implementing ScrubAndZoomEventListener, other classes can access events
 * related to user interaction such as zooming, dragging zoomed out bitmaps,
 * finger on screen and finger off screen.
 */
public class ZoomingImageView extends ImageView {

	private static final float TOUCH_DISTANCE_THRESHOLD = 3f;

	private final Matrix modifyEventViewFrameMatrix = new Matrix();
	private final Matrix initialImageViewMatrix = new Matrix();
	private final float[] values = new float[9];

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;

	private boolean isZoomed;
	public boolean isCropped;
	private float cropZoom;
	private float minimalZoom = 1;
	private float pinchScale;

	private OnScrubAndZoomEventListener scrubAndZoomListener;

	public ZoomingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ZoomingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZoomingImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		gestureDetector = new GestureDetector(getContext(), gestureListener);
		scaleDetector = new ScaleGestureDetector(getContext(), scaleListener);
	}

	public void setOnScrubAndZoomEventListener(OnScrubAndZoomEventListener scrubAndZoomListener) {
		this.scrubAndZoomListener = scrubAndZoomListener;
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (drawable == null) {
			return;
		}

		// Get current position of old bitmap
		getImageMatrix().getValues(values);
		float formerTransX = values[Matrix.MTRANS_X];
		float formerTransY = values[Matrix.MTRANS_Y];

		// Calculate matrix to fit new bitmap to ImageView keeping aspect ratio
		initialImageViewMatrix.reset();
		RectF drawableRect = new RectF(0, 0, (float) drawable.getIntrinsicWidth(), (float) drawable.getIntrinsicHeight());
		RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
		initialImageViewMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

		// Obtain scale and position of new bitmap from calculated matrix
		initialImageViewMatrix.getValues(values);
		float newScale = Math.min(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
		float newTransX = values[Matrix.MTRANS_X];

		// Calculate ratio between initial zoom of old and new bitmap
		float ratio = minimalZoom / newScale;

		// Copy calculated matrix for future operations
		Matrix keepZoom = new Matrix(initialImageViewMatrix);

		// Compute additional zoom so bitmap fills entire ImageView
		cropZoom = (newTransX == 0) ? getHeight() / (drawable.getIntrinsicHeight() * newScale) : getWidth() / (drawable.getIntrinsicWidth() * newScale);

		// Correct image scale accounting if the image is currently cropped and/or zoomed
		// If image is not cropped and not zoomed, just apply calculated initial matrix
		if (isZoomed) {
			keepZoom.postScale(pinchScale / minimalZoom, pinchScale / minimalZoom);
			keepZoom.getValues(values);
			keepZoom.postTranslate(formerTransX - values[Matrix.MTRANS_X], formerTransY - values[Matrix.MTRANS_Y]);
			pinchScale = pinchScale / ratio;

			// Trigger onZoom event
			if (scrubAndZoomListener != null) {
				scrubAndZoomListener.onZoom();
			}

		} else if (isCropped) {
			keepZoom.postScale(cropZoom, cropZoom, getWidth() / 2f, getHeight() / 2f);
			pinchScale = cropZoom * newScale;

		} else {
			pinchScale = newScale;
		}

		// Update minimal zoom and bitmap in ImageView
		minimalZoom = newScale;
		super.setImageDrawable(drawable);
		setImageMatrix(keepZoom);
	}

	private boolean hasMultipleFingersOnScreen;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getPointerCount() > 1) {
			scaleDetector.onTouchEvent(event);
			hasMultipleFingersOnScreen = true;
		} else {
			gestureDetector.onTouchEvent(event);
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			hasMultipleFingersOnScreen = false;
		}

		if (scrubAndZoomListener != null) {
			scrubAndZoomListener.onImageTouchEvent(event);
		}

		return true;
	}

	private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			// Triggers onTap event
			if (scrubAndZoomListener != null) {
				scrubAndZoomListener.onTap();
			}

			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {

			isCropped = !isCropped;

			// Returns image to initial position and scaling
			if (isCropped) {
				modifyEventViewFrameMatrix.set(initialImageViewMatrix);
				modifyEventViewFrameMatrix.postScale(cropZoom, cropZoom, getWidth() / 2f, getHeight() / 2f);
				setImageMatrix(modifyEventViewFrameMatrix);
				pinchScale = minimalZoom * cropZoom;

			} else {
				setImageMatrix(initialImageViewMatrix);
				pinchScale = minimalZoom;
			}
			isZoomed = false;

			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			// If image is zoomed, translate it
			if (isZoomed) {

				// Get current position of bitmap on imageView
				modifyEventViewFrameMatrix.set(getImageMatrix());
				modifyEventViewFrameMatrix.getValues(values);
				final PointF translatePosition = new PointF(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y]);

				// Calculate bitmap size considering the zoom scale
				if (getDrawable() != null) {
					final float imageWidth = getDrawable().getIntrinsicWidth() * values[Matrix.MSCALE_X];
					final float imageHeight = getDrawable().getIntrinsicHeight() * values[Matrix.MSCALE_Y];

					// Get finger drag distance on both axis and limit it
					final float deltaX = limitBitmapDragDistance(-distanceX, translatePosition.x, getWidth(), imageWidth);
					final float deltaY = limitBitmapDragDistance(-distanceY, translatePosition.y, getHeight(), imageHeight);

					modifyEventViewFrameMatrix.postTranslate(deltaX, deltaY);
				}
				setImageMatrix(modifyEventViewFrameMatrix);

			} else {

				// If image is not zoomed, trigger onDrag event
				if (scrubAndZoomListener != null && Math.abs(distanceX) > TOUCH_DISTANCE_THRESHOLD) {
					scrubAndZoomListener.onDrag(-distanceX);
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			if (scrubAndZoomListener != null && !hasMultipleFingersOnScreen) {
				scrubAndZoomListener.onLongPress();
			}
		}
	};

	private final ScaleGestureDetector.SimpleOnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			// Get current distance between fingers on screen
			final float scale = detector.getScaleFactor();

			// If zooming out beyond minimal zoom, limit to minimal zoom
			if (pinchScale * scale < minimalZoom) {
				pinchScale = minimalZoom;

				// Image is reset based on whether the bitmap is currently cropped or not
				Matrix resetMatrix = new Matrix(initialImageViewMatrix);
				setImageMatrix(resetMatrix);
				isZoomed = false;

			} else {
				// Scale bitmap based on finger distance
				modifyEventViewFrameMatrix.set(getImageMatrix());
				modifyEventViewFrameMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
				pinchScale = scale * pinchScale;
				setImageMatrix(modifyEventViewFrameMatrix);
				isZoomed = true;

				// Sets cropped state based on current zoom level
				isCropped = pinchScale > cropZoom * minimalZoom;

				if (scrubAndZoomListener != null) {
					scrubAndZoomListener.onZoom();
				}
			}

			return true;
		}
	};

	/**
	 * Limit bitmap movement by drag motion. If bitmap is smaller than its
	 * container: 1. The drag movement should not result in the image bitmap
	 * leaving its container. 2. In case bitmap is already outside container,
	 * only movement towards entering container is allowed. If bitmap is bigger
	 * than its container: 1. The drag movement should not result in the image
	 * bitmap entering its container. 2. In case bitmap is already inside
	 * container, only movement towards leaving container is allowed.
	 */
	private float limitBitmapDragDistance(float dragDistance, float bitmapStartPosition, int containerSize, float bitmapSize) {
		final boolean isBitmapSmaller = bitmapSize < containerSize;
		final float bitmapEndPosition = bitmapStartPosition + bitmapSize;

		if (isBitmapSmaller) {
			if (bitmapStartPosition <= 0 && bitmapStartPosition + dragDistance < bitmapStartPosition) {
				return 0;
			} else if (bitmapEndPosition + dragDistance > containerSize) {
				return containerSize - bitmapEndPosition;
			}
		} else {
			if (bitmapStartPosition >= 0 && bitmapStartPosition + dragDistance > bitmapStartPosition) {
				return 0;
			} else if (bitmapEndPosition + dragDistance < containerSize) {
				return containerSize - bitmapEndPosition;
			}
		}

		return dragDistance;
	}

	/**
	 * Used to receive events from ZoomingImageView whenever bitmap is zoomed,
	 * dragged or when the user's finger touches or leaves screen
	 */
	public interface OnScrubAndZoomEventListener {

		void onZoom();

		void onDrag(float dragDistance);

		void onTap();

		void onLongPress();

		void onImageTouchEvent(MotionEvent event);
	}
}
