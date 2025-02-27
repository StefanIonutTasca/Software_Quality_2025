package  org.jabberpoint.presentation ;

import java.awt.Color;
import java.awt.Font;

/**
 * Theme class represents styling options for a presentation.
 * This class is deliberately not formatted according to Google Java Format
 * to demonstrate the automatic formatting workflow.
 */
public   class   Theme {
    private   String   name;
    private Color   backgroundColor;
    private Color   textColor;
    private   Font   titleFont;
    private  Font  bodyFont;

    public  Theme(String name, Color backgroundColor, Color textColor,
        Font titleFont,Font bodyFont) {
        this.name   =  name;
        this.backgroundColor   =  backgroundColor;
        this.textColor  =   textColor;
        this.titleFont  =  titleFont;
        this.bodyFont = bodyFont;
    }

    // Default themes
    public static Theme createDefaultTheme() {
    return new Theme(
            "Default",
        Color.WHITE,
            Color.BLACK,
        new Font("Arial", Font.BOLD, 24),
            new Font("Arial", Font.PLAIN, 18)
    );
    }

    public static Theme  createDarkTheme() {
        return new Theme(
            "Dark",
                Color.BLACK,
            Color.WHITE,
                new Font("Arial", Font.BOLD, 24),
            new Font("Arial", Font.PLAIN, 18)
        );
    }

    // Getters and setters with inconsistent formatting
    public String getName() { return name; }
    
    public  void  setName(String name)  {
        this.name  =  name;
    }

    public Color getBackgroundColor(){return backgroundColor;}
    
    public   void setBackgroundColor  (Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color  getTextColor() {
    return textColor;
    }
    
    public void setTextColor  (  Color textColor ) {
        this.textColor = textColor;
    }

    public Font  getTitleFont() { return  titleFont; }
    
    public  void  setTitleFont(Font  titleFont) {
        this.titleFont = titleFont;
    }

    public Font getBodyFont(){return bodyFont;}
    
    public void  setBodyFont(Font bodyFont){
    this.bodyFont = bodyFont;
    }
}
