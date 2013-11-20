package core.september.textmesecure.fragments.models;

public class CheckBoxModel {

		  private String name;
		  private boolean selected;
		  private String login;

		  public CheckBoxModel(String name, String login) {
		    this.name = name;
		    this.login = login;
		    selected = false;
		  }

		  public String getName() {
		    return name;
		  }

		  public void setName(String name) {
		    this.name = name;
		  }
		  
		  public String getLogin() {
			    return this.login;
			  }

		  public boolean isSelected() {
		    return selected;
		  }

		  public void setSelected(boolean selected) {
		    this.selected = selected;
		  }

}
