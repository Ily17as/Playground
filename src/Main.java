import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class which contains <b>main method</b>
 * and <b>checks</b> for all <b>exceptions</b>.
 * Stores <u>game board</u> and arrays of its components.
 * @see Main#main(String[])
 * @see Main#checkBoard(Board, EntityPosition)
 * @see Main#checkColor(String)
 * @see Main#checkDuple(String, String)
 * @see Main#checkSize(int)
 * @see Main#checkPosition(EntityPosition, int)
 * @see Main#checkNumOfFood(int)
 * @see Main#checkNumOfIns(int)
 */
public class Main {
    private static final int FOUR = 4;
    private static final int SIXTEEN = 16;
    private static final int TWO_HUNDRED = 200;
    private static final int THOUSAND = 1000;
    private static final int THREE = 3;

    /**
     * Game board where all actions are provided.
     */
    private static Board gameBoard;

    /**
     * List of all insects at the game board.
     */
    private static final ArrayList<Insect> INSECTS = new ArrayList<>();

    /**
     * List of <b>red</b> insects. Needed for check duplications on the game board.
     */
    private static final ArrayList<String> RED = new ArrayList<>();

    /**
     * List of <b>green</b> insects. Needed for check duplications on the game board.
     */
    private static final ArrayList<String> GREEN = new ArrayList<>();

    /**
     * List of <b>blue</b> insects. Needed for check duplications on the game board.
     */
    private static final ArrayList<String> BLUE = new ArrayList<>();

    /**
     * List of <b>yellow</b> insects. Needed for check duplications on the game board.
     */
    private static final ArrayList<String> YELLOW = new ArrayList<>();

    /**
     * Main method: reads data from <b>input file</b> and checks it values.
     * After reading all data provides to <b>output file</b> the answer if
     * there are no mistakes.
     * @param args commands and inputs needed for them.
     * @throws IOException If there are no files with provided names,
     * program will end with exception.
     */
    public static void main(String[] args) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader("input.txt"))) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"))) {
                gameBoard = new Board(checkSize(Integer.parseInt(in.readLine())));
                int numberOfInsects = checkNumOfIns(Integer.parseInt(in.readLine()));
                int numberOfFoodPoints = checkNumOfFood(Integer.parseInt(in.readLine()));
                for (int i = 0; i < numberOfInsects; i++) {
                    String[] line = in.readLine().split(" ");
                    int x = Integer.parseInt(line[2]);
                    int y = Integer.parseInt(line[THREE]);
                    checkType(line[1]);
                    checkColor(line[0]);
                    checkPosition(new EntityPosition(x, y), gameBoard.getSize());
                    checkDuple(line[1], line[0]);
                    checkBoard(gameBoard, new EntityPosition(x, y));
                    switch (line[1]) {
                        case ("Grasshopper"):
                            Grasshopper grasshopper = new Grasshopper(new EntityPosition(x, y), InsectColor.toColor(line[0]));
                            gameBoard.addEntity(grasshopper);
                            INSECTS.add(grasshopper);
                            break;
                        case ("Butterfly"):
                            Butterfly butterfly = new Butterfly(new EntityPosition(x, y), InsectColor.toColor(line[0]));
                            gameBoard.addEntity(butterfly);
                            INSECTS.add(butterfly);
                            break;
                        case ("Ant"):
                            Ant ant = new Ant(new EntityPosition(x, y), InsectColor.toColor(line[0]));
                            gameBoard.addEntity(ant);
                            INSECTS.add(ant);
                            break;
                        case ("Spider"):
                            Spider spider = new Spider(new EntityPosition(x, y), InsectColor.toColor(line[0]));
                            gameBoard.addEntity(spider);
                            INSECTS.add(spider);
                            break;
                        default:
                    }
                }
                for (int i = 0; i < numberOfFoodPoints; i++) {
                    String[] s = in.readLine().split(" ");
                    int amountOfFood = Integer.parseInt(s[0]);
                    int x = Integer.parseInt(s[1]);
                    int y = Integer.parseInt(s[2]);
                    checkPosition(new EntityPosition(x, y), gameBoard.getSize());
                    checkBoard(gameBoard, new EntityPosition(x, y));
                    gameBoard.addEntity(new FoodPoint(new EntityPosition(x, y), amountOfFood));
                }
                for (Insect ins : INSECTS) {
                    String col = ins.color.toStr(ins.color);
                    String clas = ins.getClass().toString().split(" ")[1];
                    String dir = gameBoard.getDirection(ins).getTextRepresentation();
                    int value = gameBoard.getDirectionSum(ins);
                    out.write(col + " " + clas + " " + dir + " " + value + "\n");
                }
                in.close();
                out.close();
            }
        } catch (InvalidEntityPositionException | InvalidInsectTypeException
                 | InvalidInsectColorException | InvalidNumberOfInsectsException
                 | InvalidNumberOfFoodPointsException | TwoEntitiesOnSamePositionException
                 | InvalidBoardSizeException | DuplicateInsectException e) {
            BufferedWriter problem = new BufferedWriter(new FileWriter("output.txt"));
            problem.write(e.getMessage());
            problem.close();
            System.exit(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.exit(0);
    }

    /**
     * Checks the input size of the board <b>4 ≤ size ≤ 1000</b>.
     * @param size size of the board.
     * @return Size of the board if correct.
     * @throws InvalidBoardSizeException
     */
    public static int checkSize(int size) throws InvalidBoardSizeException {
        if (size < FOUR || size > THOUSAND) {
            throw new InvalidBoardSizeException();
        } else {
            return size;
        }
    }

    /**
     * Checks the input number of insects <b>1 ≤ N ≤ 16</b>.
     * @param number number of Insects.
     * @return number of Insects if correct.
     * @throws InvalidNumberOfInsectsException
     */
    public static int checkNumOfIns(int number) throws InvalidNumberOfInsectsException {
        if (number < 1 || number > SIXTEEN) {
            throw new InvalidNumberOfInsectsException();
        } else {
            return number;
        }
    }

    /**
     * Checks the input number of food points <b>1 ≤ number ≤ 200</b>.
     * @param number number of food points.
     * @return Number of food points if correct.
     * @throws InvalidNumberOfFoodPointsException
     */
    public static int checkNumOfFood(int number) throws InvalidNumberOfFoodPointsException {
        if (number < 1 || number > TWO_HUNDRED) {
            throw new InvalidNumberOfFoodPointsException();
        } else {
            return number;
        }
    }

    /**
     * Checks if there is any entity already in the provided position.
     * @param board game board.
     * @param position provided entity position.
     * @throws TwoEntitiesOnSamePositionException
     */
    public static void checkBoard(Board board, EntityPosition position) throws TwoEntitiesOnSamePositionException {
        for (EntityPosition per : board.getBoardData().keySet()) {
            if (per.getX() == position.getX() && per.getY() == position.getY()) {
                throw new TwoEntitiesOnSamePositionException();
            }
        }
    }

    /**
     * Checks provided from input file <b>color</b> of insect if it is one of this colors
     * <b>{Red, Green, Blue, Yellow}</b>.
     * @param s string with color of insect that provided in input file.
     * @throws InvalidInsectColorException
     */
    public static void checkColor(String s) throws InvalidInsectColorException {
        String r = "Red";
        String g = "Green";
        String b = "Blue";
        String y = "Yellow";
        if (!s.equals(r) && !s.equals(g) && !s.equals(b) && !s.equals(y)) {
            throw new InvalidInsectColorException();
        }
    }

    /**
     * Checks provided from input file <b>type</b> of insect if it is one of this types
     * <b>{Ant, Butterfly, Spider, Grasshopper}</b>.
     * @param t string type of insect provided in input file.
     * @throws InvalidInsectTypeException
     */
    public static void checkType(String t) throws InvalidInsectTypeException {
        String a = "Ant";
        String b = "Butterfly";
        String s = "Spider";
        String g = "Grasshopper";
        if (!t.equals(a) && !t.equals(b) && !t.equals(s) && !t.equals(g)) {
            throw new InvalidInsectTypeException();
        }
    }

    /**
     * Checks if position is on the board.
     * @param pos position of entity.
     * @param boardSize size of the board.
     * @throws InvalidEntityPositionException
     */
    public static void checkPosition(EntityPosition pos, int boardSize) throws InvalidEntityPositionException {
        if (pos.getY() > boardSize || pos.getY() < 1 || pos.getX() > boardSize || pos.getX() < 1) {
            throw new InvalidEntityPositionException();
        }
    }

    /**
     * Checks if there are duplicates among insects.
     * @param insect string type of insect.
     * @param color color of the insect.
     * @throws DuplicateInsectException
     */
    public static void checkDuple(String insect, String color) throws DuplicateInsectException {
        switch (color) {
            case ("Red"):
                if (RED.contains(insect)) {
                    throw new DuplicateInsectException();
                } else {
                    RED.add(insect);
                }
                break;
            case ("Blue"):
                if (BLUE.contains(insect)) {
                    throw new DuplicateInsectException();
                } else {
                    BLUE.add(insect);
                }
                break;
            case ("Green"):
                if (GREEN.contains(insect)) {
                    throw new DuplicateInsectException();
                } else {
                    GREEN.add(insect);
                }
                break;
            case ("Yellow"):
                if (YELLOW.contains(insect)) {
                    throw new DuplicateInsectException();
                } else {
                    YELLOW.add(insect);
                }
                break;
            default:
        }

    }
}

/**
 * Exception to two entities in the same position.
 * Prints <b>"Two entities in the same position"</b>.
 * Extends java.lang.Exception
 */
class TwoEntitiesOnSamePositionException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Two entities in the same position";
    }
}

/**
 * Exception to invalid number of insects.
 * Prints <b>"Invalid number of insects"</b>.
 * Extends java.lang.Exception
 */
class InvalidNumberOfInsectsException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Invalid number of insects";
    }
}

/**
 * Exception to invalid number of food points.
 * Prints <b>"Invalid number of food points"</b>.
 * Extends java.lang.Exception
 */
class InvalidNumberOfFoodPointsException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Invalid number of food points";
    }
}

/**
 * Exception to invalid insect type.
 * Prints <b>"Invalid insect type"</b>.
 * Extends java.lang.Exception
 */
class InvalidInsectTypeException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Invalid insect type";
    }
}

/**
 * Exception to invalid insect color.
 * Prints <b>"Invalid insect color"</b>.
 * Extends java.lang.Exception
 */
class InvalidInsectColorException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Invalid insect color";
    }
}

/**
 * Exception to invalid entity position.
 * Prints <b>"Invalid entity position"</b>.
 * Extends java.lang.Exception
 */
class InvalidEntityPositionException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Invalid entity position";
    }
}

/**
 * Exception to invalid board size.
 * Prints <b>"Invalid board size"</b>.
 * Extends java.lang.Exception
 */
class InvalidBoardSizeException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}

/**
 * Exception to duplicate of the insect on the board.
 * Prints <b>"Duplicate insects"</b>.
 * Extends java.lang.Exception
 */
class DuplicateInsectException extends java.lang.Exception {
    @Override
    public String getMessage() {
        return "Duplicate insects";
    }
}

/**
 * Class of insects of "Ant" type.
 * Implements DiagonalMoving, OrthogonalMoving
 * Extends Insect
 * @see Ant#Ant(EntityPosition, InsectColor)
 */
class Ant extends Insect implements DiagonalMoving, OrthogonalMoving {

    /**
     * Constructor for object of Ant class.
     * @param position position on the board.
     * @param color color of the ant.
     */
    Ant(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    /**
     * Function used to get values of possible <b>diagonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int directionValue = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        EntityPosition newPos = move(x, y, dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        directionValue += ((FoodPoint) boardData.get(key)).value;
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        return directionValue;
    }

    /**
     * Function that provides the action of moving for insects in <b>diagonal way</b>:
     * changes board data, removes food points(if they were eaten)
     * and provided insect.
     * @param dir direction for path.
     * @param entityPosition position of the insect.
     * @param color color of the insect.
     * @param boardData data of board entities.
     * @param boardSize size of the board.
     * @return Integer amount of eaten food.
     */
    @Override
    public int travelDiagonally(Direction dir,  EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int totalAmount = 0;
        ArrayList<EntityPosition> removable = new ArrayList<>();
        EntityPosition newPos = move(entityPosition.getX(), entityPosition.getY(), dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        totalAmount += ((FoodPoint) boardData.get(key)).value;
                        removable.add(key);
                    } else if (boardData.get(key) instanceof Insect) {
                        if (!((Insect) boardData.get(key)).color.name().equals(color.name())) {
                            boardData.remove(entityPosition);
                            for (EntityPosition i : removable) {
                                boardData.remove(i);
                            }
                            return totalAmount;
                        }
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        boardData.remove(entityPosition);
        for (EntityPosition i : removable) {
            boardData.remove(i);
        }
        return totalAmount;
    }

    /**
     * Function that selects the best way of all possible directions.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return The best possible direction.
     */
    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int maxOfN = getOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int maxOfE = getOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int maxOfS = getOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int maxOfW = getOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize);
        int maxOfNE = getDiagonalDirectionVisibleValue(Direction.NE, entityPosition, boardData, boardSize);
        int maxOfNW = getDiagonalDirectionVisibleValue(Direction.NW, entityPosition, boardData, boardSize);
        int maxOfSE = getDiagonalDirectionVisibleValue(Direction.SE, entityPosition, boardData, boardSize);
        int maxOfSW = getDiagonalDirectionVisibleValue(Direction.SW, entityPosition, boardData, boardSize);
        int[] directionValues = {maxOfN, maxOfE, maxOfS, maxOfW, maxOfNE, maxOfSE, maxOfSW, maxOfNW};
        int best = max(directionValues);
        if (best == maxOfN) {
            return Direction.N;
        } else if (best == maxOfE) {
            return Direction.E;
        } else if (best == maxOfS) {
            return Direction.S;
        } else if (best == maxOfW) {
            return Direction.W;
        } else if (best == maxOfNE) {
            return Direction.NE;
        } else if (best == maxOfNW) {
            return Direction.NW;
        } else if (best == maxOfSE) {
            return Direction.SE;
        } else {
            return Direction.SW;
        }
    }

    /**
     * Function that makes <u>travel</u> action of <b>all possible ways</b>.
     * @param dir direction of the path.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return Integer amount eaten food for insect.
     * @see DiagonalMoving#travelDiagonally(Direction, EntityPosition, InsectColor, Map, int)
     * @see OrthogonalMoving#travelOrthogonal(Direction, EntityPosition, InsectColor, Map, int)
     */
    @Override
    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        if (dir == Direction.E || dir == Direction.S || dir == Direction.N || dir == Direction.W) {
            return this.travelOrthogonal(dir, this.entityPosition, this.color, boardData, boardSize);
        } else {
            return this.travelDiagonally(dir, this.entityPosition, this.color, boardData, boardSize);
        }
    }

    /**
     * Function used to get values of possible <b>orthogonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int directionValue = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        EntityPosition newPos = move(x, y, dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        directionValue += ((FoodPoint) boardData.get(key)).value;
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        return directionValue;
    }

    /**
     * Function that provides the action of moving for insects in <b>orthogonal way</b>:
     * changes board data, removes food points(if they were eaten)
     * and provided insect.
     * @param dir direction for path.
     * @param entityPosition position of the insect.
     * @param color color of the insect.
     * @param boardData data of board entities.
     * @param boardSize size of the board.
     * @return Integer amount of eaten food.
     */
    @Override
    public int travelOrthogonal(Direction dir, EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int totalAmount = 0;
        ArrayList<EntityPosition> removable = new ArrayList<>();
        EntityPosition newPos = move(entityPosition.getX(), entityPosition.getY(), dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        totalAmount += ((FoodPoint) boardData.get(key)).value;
                        removable.add(key);
                    } else if (boardData.get(key) instanceof Insect) {
                        if (!((Insect) boardData.get(key)).color.name().equals(color.name())) {
                            boardData.remove(entityPosition);
                            for (EntityPosition i : removable) {
                                boardData.remove(i);
                            }
                            return totalAmount;
                        }
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        boardData.remove(entityPosition);
        for (EntityPosition i : removable) {
            boardData.remove(i);
        }
        return totalAmount;
    }
}

/**
 * Class used to declare boards. Contains boardData, and size.
 * @see Board#Board(int)
 * @see Board#addEntity(BoardEntity)
 * @see Board#getDirection(Insect)
 * @see Board#getDirectionSum(Insect)
 */
class Board {
    /**
     * Map that contains information about entities on the board.
     */
    private Map<EntityPosition, BoardEntity> boardData = new HashMap<>();
    /**
     * Size of the board (n x n).
     */
    private final int size;

    /**
     * Constructor for board.
     * @param boardSize size of the board.
     */
    Board(int boardSize) {
        this.size = boardSize;
    }

    /**
     * Function for adding some entity on the board.
     * @param entity insect or food point that needed to add on board.
     */
    public void addEntity(BoardEntity entity) {
        this.boardData.put(entity.entityPosition, entity);
    }

    /**
     * Function used to get entity from the board by its position.
     * @param position entity position on the board
     * @return Board entity
     */
    public BoardEntity getEntity(EntityPosition position) {
        return this.boardData.get(position);
    }

    /**
     * Function used to get direction selected by insect (most valuable or primary).
     * @param insect insect that is on the board.
     * @return Selected direction.
     */
    public Direction getDirection(Insect insect) {
        return insect.getBestDirection(boardData, size);
    }

    /**
     * Function used to get the value of the path traveled by some insect.
     * @param insect insect type entity which is on the board.
     * @return Value of selected path.
     */
    public int getDirectionSum(Insect insect) {
        return insect.travelDirection(getDirection(insect), boardData, size);
    }

    /**
     * Getter for board size.
     * @return Integer size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Getter for board data.
     * @return data of entities on the board.
     */
    public Map<EntityPosition, BoardEntity> getBoardData() {
        return boardData;
    }
}

/**
 * Abstract class which shows that entity refers to board.
 * @see BoardEntity#entityPosition
 */
abstract class BoardEntity {
    /**
     * Shows position of board entity. Consists integers <b>x</b> and <b>y</b>.
     */
    protected EntityPosition entityPosition;
}

/**
 * Class of butterflies needed to identify the type of the insect on the board.
 * Uses Insect class functions + its own constructor.
 * Extends Insect.
 * Implements OrthogonalMoving.
 * @see Insect
 * @see Butterfly#Butterfly(EntityPosition, InsectColor)
 */
class Butterfly extends Insect implements OrthogonalMoving {
    /**
     * Constructor for butterfly insect.
     * @param position position on the board.
     * @param color color of butterfly.
     */
    Butterfly(EntityPosition position, InsectColor color) {
        super(position, color);
    }
    /**
     * Function used to get values of possible <b>orthogonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    @Override
    public int travelOrthogonal(Direction dir, EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int totalAmount = 0;
        ArrayList<EntityPosition> removable = new ArrayList<>();
        EntityPosition newPos = move(entityPosition.getX(), entityPosition.getY(), dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        totalAmount += ((FoodPoint) boardData.get(key)).value;
                        removable.add(key);
                    } else if (boardData.get(key) instanceof Insect) {
                        if (!((Insect) boardData.get(key)).color.name().equals(color.name())) {
                            boardData.remove(entityPosition);
                            for (EntityPosition i : removable) {
                                boardData.remove(i);
                            }
                            return totalAmount;
                        }
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        boardData.remove(entityPosition);
        for (EntityPosition i : removable) {
            boardData.remove(i);
        }
        return totalAmount;
    }
    /**
     * Function used to get values of possible <b>orthogonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int directionValue = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        EntityPosition newPos = move(x, y, dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        directionValue += ((FoodPoint) boardData.get(key)).value;
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        return directionValue;
    }
    /**
     * Function that makes <u>travel</u> action of <b>all possible ways</b>.
     * @param dir direction of the path.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return Integer amount eaten food for insect.
     * @see DiagonalMoving#travelDiagonally(Direction, EntityPosition, InsectColor, Map, int)
     * @see OrthogonalMoving#travelOrthogonal(Direction, EntityPosition, InsectColor, Map, int)
     */
    @Override
    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return this.travelOrthogonal(dir, this.entityPosition, this.color, boardData, boardSize);
    }
    /**
     * Function that selects the best way of all possible directions.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return The best possible direction.
     */
    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int maxOfN = getOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int maxOfE = getOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int maxOfS = getOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int maxOfW = getOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize);
        int[] directionValues = {maxOfN, maxOfE, maxOfS, maxOfW};
        int best = max(directionValues);
        if (best == maxOfN) {
            return Direction.N;
        } else if (best == maxOfE) {
            return Direction.E;
        } else if (best == maxOfS) {
            return Direction.S;
        } else {
            return Direction.W;
        }
    }

}

interface DiagonalMoving {

    /**
     * Function used to get values of possible <b>diagonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize);

    /**
     * Function that provides the action of moving for insects in <b>diagonal way</b>:
     * changes board data, removes food points(if they were eaten)
     * and provided insect.
     * @param dir direction for path.
     * @param entityPosition position of the insect.
     * @param color color of the insect.
     * @param boardData data of board entities.
     * @param boardSize size of the board.
     * @return Integer amount of eaten food.
     */
    int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize);
}

/**
 * Enum that declares the directions:
 * N - north, E - east, W - west, S - south, NE - north-east, SE - south-east, SW - south-west, NW - north-west.
 * Also has function that represent the meaning of direction in string type.
 * @see Direction#getTextRepresentation()
 */
enum Direction {
    /**
     * North.
     */
    N("North"),
    /**
     * East.
     */
    E("East"),
    /**
     * South.
     */
    S("South"),
    /**
     * West.
     */
    W("West"),
    /**
     * North-East.
     */
    NE("North-East"),
    /**
     * South-East.
     */
    SE("South-East"),
    /**
     * South-West.
     */
    SW("South-West"),
    /**
     * North-West.
     */
    NW("North-West");
    /**
     * Variable declares text representation for directions.
     */
    private final String textRepresentation;

    /**
     * Function needed to declare text representation of direction.
     * @param textRepresentation string meaning of direction.
     */
    Direction(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    /**
     * Function used to get the representation of provided direction in string type.
     * @return Text representation of provided direction.
     */
    public String getTextRepresentation() {
        return this.textRepresentation;
    }
}

/**
 * Class used to identify position on the boar by
 * <b>x</b> and <b>y</b> coordinate.
 * @see EntityPosition#EntityPosition(int, int)
 */
class EntityPosition {
    /**
     * x coordinate on the board.
     */
    private int x;
    /**
     * y coordinate on the board.
     */
    private int y;

    /**
     * Constructor for entity position.
     * @param x coordinate by x-axis.
     * @param y coordinate by y-axis.
     */
    EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for x coordinate.
     * @return Value of x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for y coordinate.
     * @return Value of y coordinate.
     */
    public int getY() {
        return y;
    }
}

/**
 * Class that is used to declare entity of <b>food</b> type.
 * Contains integer <b>value</b> of food point and <b>entity position</b> from extended class.
 * @see FoodPoint#FoodPoint(EntityPosition, int)
 * @see BoardEntity
 * Extends BoardEntity.
 */
class FoodPoint extends BoardEntity {
    /**
     * Value of food point.
     */
    protected int value;

    /**
     * Constructor for food point.
     * @param position position of the point on the board.
     * @param foodValue amount of this point.
     */
    FoodPoint(EntityPosition position, int foodValue) {
        super.entityPosition = position;
        this.value = foodValue;
    }
}

/**
 * Class represents insects of type Grasshopper with it specific possible ways.
 * @see Grasshopper#Grasshopper(EntityPosition, InsectColor)
 * @see Grasshopper#grasshopperTravel(Direction, EntityPosition, InsectColor, Map, int)
 * @see Grasshopper#grasshopperVisibleValue(Direction, EntityPosition, Map, int)
 * @see Insect
 * Extends Insect
 */
class Grasshopper extends Insect {
    /**
     * Constructor for insect of Grasshopper type.
     * @param position entity position on the board.
     * @param color color of insect.
     */
    Grasshopper(EntityPosition position, InsectColor color) {
        super(position, color);
    }
    /**
     * Function that selects the best way of all possible directions.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return The best possible direction.
     */
    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int maxOfN = grasshopperVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int maxOfE = grasshopperVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int maxOfS = grasshopperVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int maxOfW = grasshopperVisibleValue(Direction.W, entityPosition, boardData, boardSize);
        int[] directionValues = {maxOfN, maxOfE, maxOfS, maxOfW};
        int best = max(directionValues);
        if (best == maxOfN) {
            return Direction.N;
        } else if (best == maxOfE) {
            return Direction.E;
        } else if (best == maxOfS) {
            return Direction.S;
        } else {
            return Direction.W;
        }
    }
    /**
     * Function that makes <u>travel</u> action of <b>all possible ways</b>.
     * @param dir direction of the path.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return Integer amount eaten food for insect.
     * @see DiagonalMoving#travelDiagonally(Direction, EntityPosition, InsectColor, Map, int)
     * @see OrthogonalMoving#travelOrthogonal(Direction, EntityPosition, InsectColor, Map, int)
     */
    @Override
    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return this.grasshopperTravel(dir, this.entityPosition, this.color, boardData, boardSize);
    }

    /**
     * Function used to find the visible sum of values of food points for provided direction for grasshopper.
     * @param dir direction of the path.
     * @param entityPosition position of grasshopper on the board.
     * @param boardData map of data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer value that sees grasshopper for selected path.
     */
    public int grasshopperVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int directionValue = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        EntityPosition newPos = move(x, y, dir, 2);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        directionValue += ((FoodPoint) boardData.get(key)).value;
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 2);
        }
        return directionValue;
    }

    /**
     * Function made <b>travel</b> action for grasshopper:
     * removes eaten food points and grasshopper from the board.
     * @param dir direction of travel.
     * @param entityPosition position of grasshopper on the board.
     * @param color color of the grasshopper.
     * @param boardData map of data of entities on the board.
     * @param boardSize size of the board.
     * @return Amount of sum of eaten food points.
     */
    public int grasshopperTravel(Direction dir, EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int totalAmount = 0;
        ArrayList<EntityPosition> removable = new ArrayList<>();
        EntityPosition newPos = move(entityPosition.getX(), entityPosition.getY(), dir, 2);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        totalAmount += ((FoodPoint) boardData.get(key)).value;
                        removable.add(key);
                    } else if (boardData.get(key) instanceof Insect) {
                        if (!((Insect) boardData.get(key)).color.name().equals(color.name())) {
                            boardData.remove(entityPosition);
                            for (EntityPosition i : removable) {
                                boardData.remove(i);
                            }
                            return totalAmount;
                        }
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 2);
        }
        boardData.remove(entityPosition);
        for (EntityPosition i : removable) {
            boardData.remove(i);
        }
        return totalAmount;
    }
}

/**
 * Class that represents the type of board entities.
 * Abstract class needed to summary attributes of Ant, Spider, Butterfly, Grasshopper.
 * Contains color of insect.
 * Extends BoardEntity.
 * @see Ant
 * @see Grasshopper
 * @see Spider
 * @see Butterfly
 */
abstract class Insect extends BoardEntity {
    /**
     * The color type of insect.
     */
    protected InsectColor color;

    /**
     * Constructor for board entity of type insect.
     * @param position entity position on the board.
     * @param color insect color.
     */
    Insect(EntityPosition position, InsectColor color) {
        super.entityPosition = position;
        this.color = color;
    }

    /**
     * Function that selects the best way of all possible directions.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return The best possible direction.
     */
    public abstract Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize);
    /**
     * Function that makes <u>travel</u> action of <b>all possible ways</b>.
     * @param dir direction of the path.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return Integer amount eaten food for insect.
     * @see DiagonalMoving#travelDiagonally(Direction, EntityPosition, InsectColor, Map, int)
     * @see OrthogonalMoving#travelOrthogonal(Direction, EntityPosition, InsectColor, Map, int)
     */
    public abstract int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize);

    /**
     * Function that finds the max number from integer array.
     * @param a array of integers.
     * @return Max number from array.
     */
    public int max(int[] a) {
        int mx = 0;
        for (int i : a) {
            if (i > mx) {
                mx = i;
            }
        }
        return mx;
    }

    /**
     * Function used to made move action in selected direction.
     * Takes start position and changes it for provided step
     * in selected direction.
     * @param x start x coordinate.
     * @param y start y coordinate.
     * @param direction selected direction.
     * @param step amount of one step (1, 2).
     * @return New changed direction.
     */
    public EntityPosition move(int x, int y, Direction direction, int step) {
        switch (direction) {
            case N:
                return new EntityPosition(x - step, y);
            case S:
                return new EntityPosition(x + step, y);
            case E:
                return new EntityPosition(x, y + step);
            case W:
                return new EntityPosition(x, y - step);
            case NE:
                return new EntityPosition(x - step, y + step);
            case SE:
                return new EntityPosition(x + step, y + step);
            case SW:
                return new EntityPosition(x + step, y - step);
            case NW:
                return new EntityPosition(x - step, y - step);
            default:
                return null;
        }
    }
}

/**
 * Enum of possible colors.
 */
enum InsectColor {
    RED,
    GREEN,
    BLUE,
    YELLOW;

    /**
     * Function that converts string with color to the object of enum.
     * @param s name of the color.
     * @return Color from enum.
     */
    public static InsectColor toColor(String s) {
        switch (s.toLowerCase()) {
            case ("red"):
                return InsectColor.RED;
            case ("green"):
                return InsectColor.GREEN;
            case ("blue"):
                return InsectColor.BLUE;
            case ("yellow"):
                return InsectColor.YELLOW;
            default:
                return null;
        }
    }

    /**
     * function that converts provided color of enum to string.
     * @param color color from InsectColor enum.
     * @return String representation of enum object.
     */
    public static String toStr(InsectColor color) {
        String first = color.toString().toLowerCase().split("")[0];
        return color.toString().toLowerCase().replace(first, first.toUpperCase());
    }
}
interface OrthogonalMoving {

    /**
     * Function used to get values of possible <b>orthogonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize);

    /**
     * Function that provides the action of moving for insects in <b>orthogonal way</b>:
     * changes board data, removes food points(if they were eaten)
     * and provided insect.
     * @param dir direction for path.
     * @param entityPosition position of the insect.
     * @param color color of the insect.
     * @param boardData data of board entities.
     * @param boardSize size of the board.
     * @return Integer amount of eaten food.
     */
    int travelOrthogonal(Direction dir, EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize);
}

/**
 * Class that represents insects of spider type.
 * Contains position and color provided from Insect type.
 * Extends Insect.
 * Implements DiagonalMoving.
 * @see DiagonalMoving
 * @see Insect
 */
class Spider extends Insect implements DiagonalMoving {
    /**
     * Constructor for spiders.
     * @param position position of the spider on the board.
     * @param color color of spider.
     */
    Spider(EntityPosition position, InsectColor color) {
        super(position, color);
    }
    /**
     * Function that provides the action of moving for insects in <b>diagonal way</b>:
     * changes board data, removes food points(if they were eaten)
     * and provided insect.
     * @param dir direction for path.
     * @param entityPosition position of the insect.
     * @param color color of the insect.
     * @param boardData data of board entities.
     * @param boardSize size of the board.
     * @return Integer amount of eaten food.
     */
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int totalAmount = 0;
        ArrayList<EntityPosition> removable = new ArrayList<>();
        EntityPosition newPos = move(entityPosition.getX(), entityPosition.getY(), dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        totalAmount += ((FoodPoint) boardData.get(key)).value;
                        removable.add(key);
                    } else if (boardData.get(key) instanceof Insect) {
                        if (!((Insect) boardData.get(key)).color.name().equals(color.name())) {
                            boardData.remove(entityPosition);
                            for (EntityPosition i : removable) {
                                boardData.remove(i);
                            }
                            return totalAmount;
                        }
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        boardData.remove(entityPosition);
        for (EntityPosition i : removable) {
            boardData.remove(i);
        }
        return totalAmount;
    }
    /**
     * Function used to get values of possible <b>diagonal ways</b>
     * depending on food points on the board.
     * @param dir direction of the way.
     * @param entityPosition position of the insect.
     * @param boardData data of entities on the board.
     * @param boardSize size of the board.
     * @return Integer number of value of food points
     * on provided direction.
     */
    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int directionValue = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        EntityPosition newPos = move(x, y, dir, 1);
        while (newPos.getY() > 0 && newPos.getY() <= boardSize && newPos.getX() > 0 && newPos.getX() <= boardSize) {
            for (EntityPosition key : boardData.keySet()) {
                if (newPos.getX() == key.getX() && newPos.getY() == key.getY()) {
                    if (boardData.get(key) instanceof FoodPoint) {
                        directionValue += ((FoodPoint) boardData.get(key)).value;
                    }
                }
            }
            newPos = move(newPos.getX(), newPos.getY(), dir, 1);
        }
        return directionValue;
    }
    /**
     * Function that makes <u>travel</u> action of <b>all possible ways</b>.
     * @param dir direction of the path.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return Integer amount eaten food for insect.
     * @see DiagonalMoving#travelDiagonally(Direction, EntityPosition, InsectColor, Map, int)
     * @see OrthogonalMoving#travelOrthogonal(Direction, EntityPosition, InsectColor, Map, int)
     */
    @Override
    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return this.travelDiagonally(dir, this.entityPosition, this.color, boardData, boardSize);
    }
    /**
     * Function that selects the best way of all possible directions.
     * @param boardData data of all entities on the board.
     * @param boardSize size of the board.
     * @return The best possible direction.
     */
    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int maxOfNE = getDiagonalDirectionVisibleValue(Direction.NE, entityPosition, boardData, boardSize);
        int maxOfNW = getDiagonalDirectionVisibleValue(Direction.NW, entityPosition, boardData, boardSize);
        int maxOfSE = getDiagonalDirectionVisibleValue(Direction.SE, entityPosition, boardData, boardSize);
        int maxOfSW = getDiagonalDirectionVisibleValue(Direction.SW, entityPosition, boardData, boardSize);
        int[] directionValues = {maxOfNE, maxOfSE, maxOfSW, maxOfNW};
        int best = max(directionValues);
        if (best == maxOfNE) {
            return Direction.NE;
        } else if (best == maxOfNW) {
            return Direction.NW;
        } else if (best == maxOfSE) {
            return Direction.SE;
        } else {
            return Direction.SW;
        }
    }
}
