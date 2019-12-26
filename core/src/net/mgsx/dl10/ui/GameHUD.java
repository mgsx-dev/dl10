package net.mgsx.dl10.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;

public class GameHUD extends Table {
	private PlatformerEngine engine;
	
	private Label continueLabel;
	
	private final Array<Image> lifeImages = new Array<Image>();
	private final Array<Image> bonusImages = new Array<Image>();

	private Label smallBonusLabel;

	public GameHUD(final PlatformerEngine engine) {
		super(GameAssets.i.skin);
		setFillParent(true);
		
		this.engine = engine;

		Table topTable = new Table(getSkin());
		Table bottomTable = new Table(getSkin());
		
		Table leftTable = new Table(getSkin());
		Table rightTable = new Table(getSkin());
		Table midTable = new Table(getSkin());

		Table bleftTable = new Table(getSkin());
		Table brightTable = new Table(getSkin());
		Table bmidTable = new Table(getSkin());

		add(topTable).fillX().row();
		add().expand().row();
		add(bottomTable).fillX().row();
		
		topTable.add(leftTable);
		topTable.add(midTable).expandX().center();
		topTable.add(rightTable);

		bottomTable.add(bleftTable);
		bottomTable.add(bmidTable).expandX().center();
		bottomTable.add(brightTable);

		Table continueTable = new Table(getSkin());
		continueTable.add("Continue").padRight(20);
		continueLabel = continueTable.add("").getActor();
		
		rightTable.add(continueTable);
		
		Table lifeTable = new Table(getSkin());
		lifeTable.defaults().pad(10);
		for(int i=0 ; i<GameSettings.playerLifeMax ; i++){
			Image img = new Image(getSkin(), "santa");
			img.setScaling(Scaling.none);
			lifeTable.add(img);
			lifeImages.add(img);
		}
		
		rightTable.add(lifeTable);
		
		Table bonusTable = new Table(getSkin());
		bonusTable.defaults().pad(10);
		for(int i=0 ; i<GameSettings.bigBonusMax ; i++){
			Image img = new Image(getSkin(), "gift" + (i+1));
			img.setScaling(Scaling.none);
			bonusTable.add(img);
			bonusImages.add(img);
		}
		
		brightTable.add(bonusTable);
		brightTable.defaults().pad(40);
		
		rightTable.defaults().pad(40);
		
		Table smallBonusTable = new Table(getSkin());
		Image img = new Image(getSkin(), "gift-neutral");
		img.setScaling(Scaling.none);
		smallBonusTable.add(img);
		smallBonusLabel = smallBonusTable.add("x00").getActor();
		
		brightTable.add(smallBonusTable);
	}
	
	@Override
	public void act(float delta) {
		continueLabel.setText(String.valueOf(engine.playerContinues));
		
		for(int i=0 ; i<lifeImages.size ; i++){
			lifeImages.get(i).getColor().a = i < engine.playerLife ? 1 : .3f;
		}
		for(int i=0 ; i<bonusImages.size ; i++){
			bonusImages.get(i).getColor().a = engine.bigBonus.contains(i) ? 1 : .3f;
		}
		
		smallBonusLabel.setText("x" + engine.smallBonus);
		
		super.act(delta);
	}
}
