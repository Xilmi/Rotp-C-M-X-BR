package rotp.model.game;

import static rotp.model.game.IGameOptions.DIFFICULTY_NORMAL;
import static rotp.model.game.IGameOptions.SHAPE_RECTANGLE;
import static rotp.model.game.IGameOptions.SIZE_SMALL;
import static rotp.model.game.IGameOptions.getGalaxyShapeOptions;
import static rotp.model.game.IGameOptions.getGalaxySizeOptions;
import static rotp.model.game.IGameOptions.getGameDifficultyOptions;
import static rotp.model.game.IPreGameOptions.dynStarsPerEmpire;
import static rotp.model.game.IPreGameOptions.empiresSpreadingFactor;
import static rotp.ui.util.IParam.langLabel;

import java.util.Arrays;
import java.util.LinkedList;

import rotp.ui.RotPUI;
import rotp.ui.util.GlobalCROptions;
import rotp.ui.util.IParam;
import rotp.ui.util.ParamBoolean;
import rotp.ui.util.ParamInteger;
import rotp.ui.util.ParamList;
import rotp.ui.util.ParamString;
import rotp.ui.util.SpecificCROption;
import rotp.ui.UserPreferences;

public interface IGalaxyOptions extends IBaseOptsTools {

	// ==================== Galaxy Menu addition ====================
	//
	ParamInteger galaxyRandSource		= new ParamInteger(MOD_UI, "GALAXY_RAND_SOURCE",
			0, 0, Integer.MAX_VALUE, 1, 100, 10000) {
		{ loop(true); }
		@Override public Integer	set(Integer value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionMedium(true);
			return value;
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}

	};
	default int selectedGalaxyRandSource()		{ return galaxyRandSource.get(); }
	ParamBoolean showNewRaces 			= new ParamBoolean(MOD_UI, "SHOW_NEW_RACES", false) {
		@Override public void	setFromCfgValue(String value)	{
			super.setFromCfgValue(value);
			if (RotPUI.instance() != null) {
				RotPUI.setupGalaxyUI().initOpponentGuide();
				RotPUI.setupGalaxyUI().postSelectionLight(true);
			}
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionLight(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionLight(true);
		}
	};
	default boolean selectedShowNewRaces()		{ return showNewRaces.get(); }

	GlobalCROptions globalCROptions 	= new GlobalCROptions (BASE_UI, "OPP_CR_OPTIONS",
			SpecificCROption.BASE_RACE.value);
	default GlobalCROptions globalCROptions()	{ return globalCROptions; }
	default String selectedUseGlobalCROptions()	{ return globalCROptions.get(); }

	ParamBoolean useSelectableAbilities	= new ParamBoolean(BASE_UI, "SELECT_CR_OPTIONS", false);
	default boolean selectedUseSelectableAbilities()	{ return useSelectableAbilities.get(); }

	ParamString  shapeOption3   		= new ParamString(BASE_UI, "SHAPE_OPTION_3", "");
	default ParamString shapeOption3()			{ return shapeOption3; }
	default String selectedGalaxyShapeOption3()	{ return shapeOption3.get(); }

	ParamList    shapeOption2   		= new ParamList( // Duplicate Do not add the list
			BASE_UI, "SHAPE_OPTION_2")	{
		{ showFullGuide(true); }
		@Override public String	getOptionValue(IGameOptions options)	{
			return options.selectedGalaxyShapeOption2();
		}
		@Override public void setOptionValue(IGameOptions options, String newValue) {
			options.selectedGalaxyShapeOption2(newValue);
		}
		@Override public String	headerHelp(boolean sep) {
			if (listSize() == 0)
				return ("This shape do not have a secondary option<br>");
			return headerHelp(shapeSelection.get() + "_O2", sep); }
		@Override public String getLangLabel(int id) {
			if (id<0)
				return "";
			String label = super.getLangLabel(id);
			if (label != null && label.startsWith("SETUP_GALAXY_MAP_OPTION_")) {
				if (shapeOption1.get().endsWith("0"))
					label += "0";
				else
					label += "1";
			}
			return label;
		}
		@Override public String	setFromIndex(int value)	{
			super.setFromIndex(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionMedium(true);
			return get();
		}
		@Override public String	set(String value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionMedium(true);
			return get();
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
	};
	ParamList    shapeOption1   		= new ParamList( // Duplicate Do not add the list
			BASE_UI, "SHAPE_OPTION_1")	{
		{ showFullGuide(true); }
		@Override public String	getOptionValue(IGameOptions options)	{
			return options.selectedGalaxyShapeOption1();
		}
		@Override public void setOptionValue(IGameOptions options, String newValue) {
			options.selectedGalaxyShapeOption1(newValue);
		}
		@Override public String	headerHelp(boolean sep) {
			if (listSize() == 0)
				return ("This shape do not have options<br>");
			return headerHelp(shapeSelection.get() + "_O1", sep); }
		@Override public String	setFromIndex(int value)	{
			super.setFromIndex(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionMedium(true);
			return get();
		}
		@Override public String	set(String value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionMedium(true);
			return get();
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
	};
	ParamList    shapeSelection			= new ParamList( // Duplicate Do not add the list
			BASE_UI, "GALAXY_SHAPE", getGalaxyShapeOptions(),  SHAPE_RECTANGLE) {
		@Override public String getOptionValue(IGameOptions options) {
			return options.selectedGalaxyShape();
		}
		@Override public void setOptionValue(IGameOptions options, String newValue) {
			options.selectedGalaxyShape(newValue);
		}
		@Override public String	setFromIndex(int value)	{
			super.setFromIndex(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionFull(true);
			return get();
		}
		@Override public String	set(String value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionFull(true);
			return get();
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionFull(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionFull(true);
		}
	};
	ParamList    sizeSelection 			= new ParamList( // Duplicate Do not add the list
			BASE_UI, "GALAXY_SIZE", getGalaxySizeOptions(), SIZE_SMALL) {
		{ showFullGuide(false); }
		@Override public String getOptionValue(IGameOptions options) {
			return options.selectedGalaxySize();
		}
		@Override public void setOptionValue(IGameOptions options, String newValue) {
			options.selectedGalaxySize(newValue);
		}
		@Override public String name(int id) {
			String diffLbl = super.name(id);
			String label   = getLangLabel(id);
			int size = opts().numberStarSystems(label);
			if (label.equals("SETUP_GALAXY_SIZE_DYNAMIC"))
				diffLbl += " (Variable; now = " + size + ")";
			else
				diffLbl += " (" + size + ")";
			return diffLbl;
		}
		@Override public String realHelp(int id) {
			String label   = getLangLabel(id);
			if (label.equals("SETUP_GALAXY_SIZE_DYNAMIC"))
				return super.realHelp(id);
			if (label.equals("SETUP_GALAXY_SIZE_MAXIMUM"))
				return super.realHelp(id);
			int size = opts().numberStarSystems(label);
			if (size < 101)
				return langLabel("SETUP_GALAXY_SIZE_MOO1_DESC");
			if (size < 1001)
				return langLabel("SETUP_GALAXY_SIZE_UP1000_DESC");
			return langLabel("SETUP_GALAXY_SIZE_OVER1000_DESC");
		}
		@Override public String	setFromIndex(int value)	{
			super.setFromIndex(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postGalaxySizeSelection(true);
			return get();
		}
		@Override public String	set(String value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postGalaxySizeSelection(true);
			return get();
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postGalaxySizeSelection(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postGalaxySizeSelection(true);
		}
	};
	ParamList    difficultySelection	= new ParamList( // Duplicate Do not add the list
			BASE_UI, "GAME_DIFFICULTY", getGameDifficultyOptions(), DIFFICULTY_NORMAL) {
		{ showFullGuide(true); }
		@Override public String getOptionValue(IGameOptions options) {
			return options.selectedGameDifficulty();
		}
		@Override public void setOptionValue(IGameOptions options, String newValue) {
			options.selectedGameDifficulty(newValue);
		}
		@Override public String name(int id) {
			String diffLbl = super.name(id);
			String label   = getLangLabel(id);
			if (label.equals("SETUP_DIFFICULTY_CUSTOM"))
				diffLbl += " (" + Integer.toString(opts().selectedCustomDifficulty()) + "%)";
			else {
				float modifier = opts().aiProductionModifier(label);
				diffLbl += " (" + Integer.toString(Math.round(100 * modifier)) + "%)";
			}
			return diffLbl;
		}
		@Override public String	setFromIndex(int value)	{
			super.setFromIndex(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionLight(true);
			return get();
		}
		@Override public String	set(String value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionLight(true);
			return get();
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionLight(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionLight(true);
		}
	};
	ParamInteger aliensNumber 			= new ParamInteger( // Duplicate Do not add the list
			BASE_UI, "ALIENS_NUMBER", 1, 0, 49, 1, 5, 20) {
		{ isDuplicate(true); }
		@Override public Integer getOptionValue(IGameOptions options) {
			maxValue(options.maximumOpponentsOptions());
			return options.selectedNumberOpponents();
		}
		@Override public void setOptionValue(IGameOptions options, Integer newValue) {
			options.selectedOpponentRace(newValue, null);
			options.selectedNumberOpponents(newValue);
		}
		@Override public Integer defaultValue() {
			return opts().defaultOpponentsOptions();
		}
		@Override public Integer set(Integer value)	{
			super.set(value);
			if (RotPUI.instance() != null)
				RotPUI.setupGalaxyUI().postSelectionMedium(true);
			return value;
		}
		@Override public void	next()	{
			super.next();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
		@Override public void	prev()	{
			super.prev();
			RotPUI.setupGalaxyUI().postSelectionMedium(true);
		}
	};
	ParamString bitmapGalaxyLastFolder = new ParamString(BASE_UI, "BITMAP_LAST_FOLDER", UserPreferences.cfgPath())
	{	{ isCfgFile(true); }	};

	// ==================== GUI List Declarations ====================
	//
	static LinkedList<IParam> optionsGalaxy = new LinkedList<>(
			Arrays.asList(
					showNewRaces, globalCROptions, useSelectableAbilities, shapeOption3,
					galaxyRandSource, empiresSpreadingFactor,
					dynStarsPerEmpire // This one is a duplicate, but it helps readability
					));
	default LinkedList<IParam> optionsGalaxy()	{ return optionsGalaxy; }
}
