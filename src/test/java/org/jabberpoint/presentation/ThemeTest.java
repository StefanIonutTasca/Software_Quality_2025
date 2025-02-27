package  org.jabberpoint.presentation ;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.awt.Font;

public  class  ThemeTest {
    
    @Test
    public void  testCreateDefaultTheme() {
        Theme  defaultTheme  =   Theme.createDefaultTheme();
        
        assertEquals("Default", defaultTheme.getName());
        assertEquals(Color.WHITE, defaultTheme.getBackgroundColor());
        assertEquals(Color.BLACK, defaultTheme.getTextColor());
        assertNotNull(defaultTheme.getTitleFont());
        assertNotNull(defaultTheme.getBodyFont());
    }
    
    @Test
    public void  testCreateDarkTheme () {
      Theme darkTheme = Theme.createDarkTheme();
      
      assertEquals("Dark",   darkTheme.getName()  );
      assertEquals(Color.BLACK,   darkTheme.getBackgroundColor()  );
      assertEquals(Color.WHITE,   darkTheme.getTextColor()  );
      assertNotNull(  darkTheme.getTitleFont()  );
      assertNotNull(  darkTheme.getBodyFont()  );
    }
    
    @Test
    public  void  testSetterGetters () {
        Theme theme = new Theme("Custom", Color.BLUE, Color.YELLOW, 
                         new Font("Times New Roman", Font.BOLD, 28),
                             new Font("Times New Roman", Font.PLAIN, 20));
        
        // Test initial values
        assertEquals("Custom", theme.getName());
        assertEquals(Color.BLUE, theme.getBackgroundColor());
        assertEquals(Color.YELLOW, theme.getTextColor());
        
        // Test setters
        theme.setName ("Modified");
        theme.setBackgroundColor (Color.GREEN);
        theme.setTextColor (Color.RED);
        Font newTitleFont = new Font("Courier", Font.ITALIC, 26);
        Font newBodyFont = new Font("Courier", Font.PLAIN, 16);
        theme.setTitleFont (newTitleFont);
        theme.setBodyFont (newBodyFont);
        
        // Verify changes
        assertEquals ( "Modified",  theme.getName() );
        assertEquals ( Color.GREEN,  theme.getBackgroundColor() );
        assertEquals ( Color.RED,  theme.getTextColor() );
        assertEquals ( newTitleFont,  theme.getTitleFont() );
        assertEquals ( newBodyFont,  theme.getBodyFont() );
    }
}
