package util.Extension;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.function.TriConsumer;

@Log4j2
@UtilityClass
public class RobotExtension {
	public static final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	private static final Map<Character, IntArrayList> special = new HashMap<>();
	private static final Map<String, TriConsumer<Integer, Integer, Robot>> directionActions = new HashMap<>();
	
	static {
		directionActions.put("up", (x, y, robot) -> robot.mouseMove(x, y - 100));
		directionActions.put("down", (x, y, robot) -> robot.mouseMove(x, y + 100));
		directionActions.put("left", (x, y, robot) -> robot.mouseMove(x - 100, y));
		directionActions.put("right", (x, y, robot) -> robot.mouseMove(x + 100, y));
		special.put('?', IntArrayList.of(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH));
		special.put('!', IntArrayList.of(KeyEvent.VK_SHIFT, KeyEvent.VK_1));
		special.put(':', IntArrayList.of(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON));
		special.put('"', IntArrayList.of(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE));
		special.put('~', IntArrayList.of(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE));
	}
	
	public static Robot type(Robot robot, String keys) {
		for (char c : keys.toCharArray()) {
			int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
			if (KeyEvent.CHAR_UNDEFINED == keyCode) {
				throw new IllegalArgumentException(
						"Key code not found for character '" + c + "'");
      		} else if (special.containsKey(c)) {
				for (int i = 0; i < special.get(c).size(); i++) {
					robot.keyPress(special.get(c).getInt(i));
				}
				for (int i = special.get(c).size() - 1; i >= 0; i--) {
					robot.keyRelease(special.get(c).getInt(i));
				}
			} else if (Character.isUpperCase(c)) {
				robot.keyPress(KeyEvent.VK_SHIFT);
				type(robot, keyCode);
				robot.keyRelease(KeyEvent.VK_SHIFT);
			} else {
				type(robot, keyCode);
			}
		}
		return robot;
	}
	
	public static Robot type(Robot robot, int i) {
		robot.keyPress(i);
		robot.keyRelease(i);
		return robot;
	}
	
	public static Robot leftClick(Robot robot) {
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		return robot;
	}
	
	public static Robot rightClick(Robot robot) {
		robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
		return robot;
	}
	
	public static Robot enter(Robot robot) {
		type(robot, KeyEvent.VK_ENTER);
		return robot;
	}
	
	public static Robot tab(Robot robot) {
		type(robot, KeyEvent.VK_TAB);
		return robot;
	}
	
	public static Robot control(Robot robot, int i) {
		robot.keyPress(KeyEvent.VK_CONTROL);
		type(robot, i);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		return robot;
	}
	
	public static Robot shift(Robot robot, int i) {
		robot.keyPress(KeyEvent.VK_SHIFT);
		type(robot, i);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		return robot;
	}
	
	public static Robot save(Robot robot) {
		control(robot, KeyEvent.VK_S);
		return robot;
	}

	public static Robot mouseMoveString(Robot robot, String direction) {
		String[] split = direction.split(" ");
		for (String dir : split) {
			int x = MouseInfo.getPointerInfo().getLocation().x;
			int y = MouseInfo.getPointerInfo().getLocation().y;
			TriConsumer<Integer, Integer, Robot> action = directionActions.get(dir);
			if (action != null) {
				action.accept(x, y, robot);
			} else {
              log.debug("Invalid direction: \"{}\"", dir);
			}
		}
		return robot;
	}
}