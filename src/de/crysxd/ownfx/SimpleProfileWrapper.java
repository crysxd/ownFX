package de.crysxd.ownfx;

@SuppressWarnings("unused")
public class SimpleProfileWrapper {
	
	private String name;
	private long id;
	private boolean active;
	
	public SimpleProfileWrapper(Profile p, long activeId) {
		this.name = p.getName();
		this.id = p.getId();
		this.active = this.id == activeId;
		
	}
}
