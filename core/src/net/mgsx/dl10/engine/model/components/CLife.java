package net.mgsx.dl10.engine.model.components;

public class CLife {
	
	public int amount;
	public Runnable onDead;
	public Runnable onHurt;
	
	public CLife(int amount) {
		this.amount = amount;
	}

	public void decrease() {
		amount--;
		if(amount <= 0){
			if(onDead != null) onDead.run();
		}else{
			if(onHurt != null) onHurt.run();
		}
	}

}
