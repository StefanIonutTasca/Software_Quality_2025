package  org.jabberpoint.animation;

import java.awt.Component;
import java.awt.Point;
import javax.swing.JComponent;
import java.util.Random;

/**
 * Animation class for slide transitions in JabberPoint.
 * This class contains intentional code quality issues to demonstrate CI/CD pipeline.
 */
public   class   Animation {
    private   int   duration;  // Duration in milliseconds
    private String   type;  // Type of animation
    private boolean   isRunning;
    private Random random = new Random();
    
    // Magic numbers intentionally used
    private static final int DEFAULT_DURATION = 500;
    private static final String DEFAULT_TYPE = "fade";
    
    public Animation() {
        this.duration = DEFAULT_DURATION;
        this.type = DEFAULT_TYPE;
        this.isRunning = false;
    }
    
    public Animation(int duration, String type) {
        this.duration = duration;
        this.type = type;
        this.isRunning = false;
    }
    
    // Intentional code duplication
    public void startFadeAnimation(JComponent component) {
        if (isRunning) return;
        isRunning = true;
        
        // Simulate animation with Thread.sleep (bad practice)
        new Thread(() -> {
            try {
                for (float alpha = 1.0f; alpha >= 0; alpha -= 0.1f) {
                    Thread.sleep(duration / 10);
                    // This would normally update component opacity
                }
                isRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }
        }).start();
    }
    
    // More code duplication with slight variations
    public void startSlideAnimation(JComponent component) {
        if (isRunning) return;
        isRunning = true;
        
        // Simulate animation with Thread.sleep (bad practice)
        new Thread(() -> {
            try {
                Point originalLocation = component.getLocation();
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(duration / 10);
                    // This would normally update component position
                }
                isRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }
        }).start();
    }
    
    // Intentionally complex method with poor structure
    public void applyRandomEffect(JComponent component) {
        int effect = random.nextInt(3);
        switch (effect) {
            case 0:
                startFadeAnimation(component);
                break;
            case 1:
                startSlideAnimation(component);
                break;
            case 2:
                // Intentionally nested and complex
                if (component != null) {
                    if (component.isVisible()) {
                        if (component.getParent() != null) {
                            startFadeAnimation(component);
                        } else {
                            startSlideAnimation(component);
                        }
                    } else {
                        // Do nothing
                    }
                }
                break;
            default:
                break;
        }
    }
    
    // Getters and setters with inconsistent formatting
    public int getDuration() { return duration; }
    
    public  void  setDuration(int duration)  {
        this.duration  =  duration;
    }

    public String getType(){return type;}
    
    public   void setType  (String type) {
        this.type = type;
    }

    public boolean  isRunning() {
    return isRunning;
    }
}
