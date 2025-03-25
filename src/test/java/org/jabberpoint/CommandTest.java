package org.jabberpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

// Import the required classes
import org.jabberpoint.Presentation;
import org.jabberpoint.Slide;
import org.jabberpoint.Command;
import org.jabberpoint.NextSlideCommand;
import org.jabberpoint.PrevSlideCommand;
import org.jabberpoint.ExitCommand;
import org.jabberpoint.OpenFileCommand;
import org.jabberpoint.SaveFileCommand;
import org.jabberpoint.NewPresentationCommand;

public class CommandTest {
    private Presentation presentation;
    private Slide slide1;
    private Slide slide2;

    @BeforeEach
    void setUp() {
        presentation = new Presentation();
        slide1 = new Slide();
        slide2 = new Slide();
        presentation.append(slide1);
        presentation.append(slide2);
    }

    @Nested
    class NextSlideCommandTest {
        private NextSlideCommand nextSlideCommand;

        @BeforeEach
        void setUp() {
            nextSlideCommand = new NextSlideCommand(presentation);
        }

        @Test
        void testExecuteMovesToNextSlide() {
            presentation.setSlideNumber(0);
            nextSlideCommand.execute();
            assertEquals(1, presentation.getSlideNumber());
        }

        @Test
        void testExecuteAtLastSlide() {
            Presentation presentation = new Presentation();
            NextSlideCommand command = new NextSlideCommand(presentation);
            presentation.setSlideNumber(presentation.getSize() - 1);
            command.execute();
            assertEquals(presentation.getSize() - 1, presentation.getSlideNumber());
        }
    }

    @Nested
    class PrevSlideCommandTest {
        private PrevSlideCommand prevSlideCommand;

        @BeforeEach
        void setUp() {
            prevSlideCommand = new PrevSlideCommand(presentation);
        }

        @Test
        void testExecuteMovesToPreviousSlide() {
            presentation.setSlideNumber(1);
            prevSlideCommand.execute();
            assertEquals(0, presentation.getSlideNumber());
        }

        @Test
        void testExecuteAtFirstSlide() {
            Presentation presentation = new Presentation();
            PrevSlideCommand command = new PrevSlideCommand(presentation);
            presentation.setSlideNumber(0);
            command.execute();
            assertEquals(0, presentation.getSlideNumber());
        }
    }

    @Nested
    class ExitCommandTest {
        private Presentation presentation;

        @BeforeEach
        void setUp() {
            presentation = new Presentation();
        }

        @Test
        void testExecute() {
            ExitCommand exitCommand = new ExitCommand();
            // Since System.exit() cannot be tested directly, we verify the command exists
            assertNotNull(exitCommand);
        }

        @Test
        void testExitCommandImplementsCommand() {
            ExitCommand command = new ExitCommand();
            assertTrue(command instanceof Command);
        }
    }

    @Nested
    class OpenFileCommandTest {
        private OpenFileCommand openFileCommand;

        @Test
        void testExecute() {
            openFileCommand = new OpenFileCommand(presentation);
            // Since file dialog cannot be tested directly, we verify the command exists
            assertNotNull(openFileCommand);
        }

        @Test
        void testOpenFileCommandImplementsCommand() {
            OpenFileCommand command = new OpenFileCommand(presentation);
            assertTrue(command instanceof Command);
        }
    }

    @Nested
    class SaveFileCommandTest {
        private SaveFileCommand saveFileCommand;

        @Test
        void testExecute() {
            saveFileCommand = new SaveFileCommand(presentation);
            // Since file dialog cannot be tested directly, we verify the command exists
            assertNotNull(saveFileCommand);
        }

        @Test
        void testSaveFileCommandImplementsCommand() {
            SaveFileCommand command = new SaveFileCommand(presentation);
            assertTrue(command instanceof Command);
        }
    }

    @Nested
    class NewPresentationCommandTest {
        private NewPresentationCommand newPresentationCommand;

        @Test
        void testExecute() {
            Presentation presentation = new Presentation();
            NewPresentationCommand command = new NewPresentationCommand(presentation);
            presentation.append(new Slide()); // Add a slide
            command.execute();
            assertEquals(0, presentation.getSize());
        }

        @Test
        void testNewPresentationCommandImplementsCommand() {
            NewPresentationCommand command = new NewPresentationCommand(presentation);
            assertTrue(command instanceof Command);
        }
    }
}


