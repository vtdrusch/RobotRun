package programming;
import global.Fields;
import robot.RobotRun;

public class IOInstruction extends Instruction {
	int state;
	int reg;

	public IOInstruction(){
		super();
		state = Fields.OFF;
		reg = -1;
	}

	public IOInstruction(int r, int t) {
		super();
		state = t;
		reg = r;
	}

	public Instruction clone() {
		Instruction copy = new IOInstruction(state, reg);
		copy.setIsCommented( isCommented() );

		return copy;
	}
	public int execute() {
		RobotRun.getInstance().getArmModel().endEffectorState = state;
		RobotRun.getInstance().getArmModel().checkPickupCollision(RobotRun.getInstance().activeScenario);
		return 0;
	}
	public int getReg(){ return reg; }
	public int getState(){ return state; }

	public void setReg(int r){ reg = r; }

	public void setState(int s){ state = s; }

	public String[] toStringArray() {
		String[] fields = new String[2];
		// Register index
		if (reg == -1) {
			fields[0] = "IO[...] =";
		} else {
			fields[0] = String.format("IO[%d] =", reg + 1);
		}
		// Register value
		if (state == Fields.ON) {
			fields[1] = "ON";
		} else {
			fields[1] = "OFF";
		}

		return fields;
	}
}
