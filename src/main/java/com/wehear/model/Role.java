package com.wehear.model;

public class Role {
    private Long id;
    private String name;
    private String description;

    public Role() {}

    public Role(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public static class RoleBuilder {
        private Role instance = new Role();

        public RoleBuilder id(Long id) { instance.setId(id); return this; }
        public RoleBuilder name(String name) { instance.setName(name); return this; }
        public RoleBuilder description(String description) { instance.setDescription(description); return this; }

        public Role build() { return instance; }
    }

    
}
