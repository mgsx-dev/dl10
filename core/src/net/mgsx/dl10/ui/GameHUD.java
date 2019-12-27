package net.mgsx.dl10.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

	private TextureRegion santaOn;

	private TextureRegion santaOff;

	private TextureRegion bonusOff;

	private Array<TextureRegion> bonusRegions = new Array<TextureRegion>();
	
	public GameHUD(final PlatformerEngine engine) {
		super(GameAssets.i.skin);
		setFillParent(true);
		
		this.engine = engine;
		
		santaOn = getSkin().getRegion("santa");
		santaOff = getSkin().getRegion("santa-outline");
		bonusOff = getSkin().getRegion("gift-outline");
		for(int i=0 ; i<6 ; i++){
			bonusRegions .add(getSkin().getRegion("gift" + (i+1)));
		}
		
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
		
		leftTable.defaults().pad(10);
		
		topTable.add(leftTable);
		topTable.add(midTable).expandX().center();
		topTable.add(rightTable);

		bottomTable.add(bleftTable);
		bottomTable.add(bmidTable).expandX().center();
		bottomTable.add(brightTable);

		Table continueTable = new Table(getSkin());
		continueTable.add("Continue").padRight(20);
		continueLabel = continueTable.add("").getActor();
		
		bleftTable.add(continueTable);
		
		Table lifeTable = new Table(getSkin());
		lifeTable.defaults().pad(10);
		for(int i=0 ; i<GameSettings.playerLifeMax ; i++){
			Image img = new Image(new TextureRegionDrawable(santaOn));
			img.setScaling(Scaling.none);
			lifeTable.add(img);
			lifeImages.add(img);
		}
		
		rightTable.add(lifeTable);
		
		Table bonusTable = new Table(getSkin());
		bonusTable.defaults().padRight(10);
		for(int i=0 ; i<GameSettings.bigBonusMax ; i++){
			Image img = new Image(new TextureRegionDrawable(bonusRegions.get(i)));
			img.setScaling(Scaling.none);
			bonusTable.add(img);
			bonusImages.add(img);
		}
		
		brightTable.defaults().pad(10);
		leftTable.add(bonusTable);
		
		rightTable.defaults().pad(10);
		
		Table smallBonusTable = new Table(getSkin());
		Image img = new Image(getSkin(), "gift-neutral");
		img.setScaling(Scaling.none);
		smallBonusLabel = smallBonusTable.add("x0").padRight(10).getActor();
		smallBonusTable.add(img);
		
		brightTable.add(smallBonusTable);
	}
	
	@Override
	public void act(float delta) {
		continueLabel.setText("x" + String.valueOf(engine.playerContinues));
		
		for(int i=0 ; i<lifeImages.size ; i++){
			if(i < engine.playerLife){
				lifeImages.get(i).getColor().a = 1;
				((TextureRegionDrawable) lifeImages.get(i).getDrawable()).setRegion(santaOn);
			}else{
				lifeImages.get(i).getColor().a = .3f;
				((TextureRegionDrawable) lifeImages.get(i).getDrawable()).setRegion(santaOff);
			}
		}
		for(int i=0 ; i<bonusImages.size ; i++){
			if(engine.bigBonus.contains(i)){
				bonusImages.get(i).getColor().a = 1;
				((TextureRegionDrawable) bonusImages.get(i).getDrawable()).setRegion(bonusRegions.get(i));
			}else{
				bonusImages.get(i).getColor().a = .3f;
				((TextureRegionDrawable) bonusImages.get(i).getDrawable()).setRegion(bonusOff);
			}
		}
		
		smallBonusLabel.setText("x" + engine.smallBonus);
		
		super.act(delta);
	}
}
