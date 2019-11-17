package lunch.g5;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import javafx.util.Pair; 
import java.util.ArrayList;
import java.util.Arrays;

import lunch.sim.Point;
import lunch.sim.Command;
import lunch.sim.CommandType;
import lunch.sim.Animal;
import lunch.sim.Family;
import lunch.sim.Food;
import lunch.sim.FoodType;
import lunch.sim.PlayerState;

public class Player implements lunch.sim.Player
{
	private int seed;
	private Random random;
	private Integer id;
	private Integer turn;
	private String avatars;
	private int dir_x;
	private int dir_y;
	private FoodType searching;

	public Player()
	{
		turn = 0;
	}

	public String init(ArrayList<Family> members, Integer id, int f,ArrayList<Animal> animals, Integer m, Integer g, double t, Integer s)
	{
		this.id = id;
		avatars = "flintstone";
		random = new Random(s);
		// choose a corner randomly
		dir_x = Math.random() > 0.5 ? 1 : -1;
		dir_y = Math.random() > 0.5 ? 1 : -1;
		return avatars;
	}

	public Command getCommand(ArrayList<Family> members, ArrayList<Animal> animals, PlayerState ps)
	{
		// go to the corner
		if(turn < 70) {
			Point next_move = new Point(ps.get_location().x + dir_x * Math.cos(Math.PI/4), ps.get_location().y + dir_y * Math.cos(Math.PI/4));
			turn++;
			return Command.createMoveCommand(next_move);
		}
		// [surrounded by monkey] or [has close goose while holding or searching sandwich]
		if(Utilities.monkey_surround(animals, ps.get_location()) ||
				(Utilities.goose_close(animals, ps.get_location()) && 
						(ps.get_held_item_type() == FoodType.SANDWICH || 
						(ps.is_player_searching() && searching == FoodType.SANDWICH)))) {
			// abort if searching
			if(ps.is_player_searching() && ps.get_held_item_type() == null) {
				System.out.println("ABORT");
				return new Command(CommandType.ABORT);
			}
			// put back if holding
			if(!ps.is_player_searching() && ps.get_held_item_type() != null) {
				System.out.println("KEEP_BACK");
				return new Command(CommandType.KEEP_BACK);
			}
		}
		// safe
		else {
			// take out if neither searching nor holding
			if(!ps.is_player_searching() && ps.get_held_item_type()==null) {
				for(FoodType food_type: FoodType.values()) {
					if(ps.check_availability_item(food_type)) {
						searching = food_type;
						System.out.println("TAKE_OUT");
						return new Command(CommandType.TAKE_OUT, food_type);
					}
				}
			}
			// eat if holding
			if(!ps.is_player_searching() && ps.get_held_item_type() != null) {
				System.out.println("EAT");
				return new Command(CommandType.EAT);
			}
		}
		return new Command();
	}
}
