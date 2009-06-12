package net.nycjava.skylight.view;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SkillTestAnimationView extends View {

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	private Context context;
	
	private AnimationDrawable frameAnimation;
	
	private BalancedObjectObserver balancedObjectObserver;

	public SkillTestAnimationView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		context = c;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		balancedObjectObserver = new BalancedObjectObserver() {

			public void balancedObjectNotification(
					float directionOfFallingInRadians,
					float anAngleOfLeanInRadians) {
								
			}

			public void fallenOverNotification() {

				 getFrameAnimation().start();
				
			}

		};
		balancedObjectPublicationService.addObserver(balancedObjectObserver);

	}

	@Override
	protected void onDetachedFromWindow() {
		balancedObjectPublicationService.removeObserver(balancedObjectObserver);

		super.onDetachedFromWindow();
	}

	public void onDraw(Canvas canvas) {
	}

	public void setFrameAnimation(AnimationDrawable frameAnimation) {
		this.frameAnimation = frameAnimation;
	}

	public AnimationDrawable getFrameAnimation() {
		return frameAnimation;
	}
}
