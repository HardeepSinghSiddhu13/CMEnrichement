package com.samsung.nmt.cmenrichment.dto;

public class NwPropMetadata {
    private Integer propertyMetadataId;
    private Integer typeId;
    private String name;

    public NwPropMetadata(Integer propertyMetadataId, Integer typeId, String name) {
        super();
        this.propertyMetadataId = propertyMetadataId;
        this.typeId = typeId;
        this.name = name;
    }

    public NwPropMetadata(Integer typeId, String name) {
        super();
        this.typeId = typeId;
        this.name = name;
    }

    public NwPropMetadata() {
    }

    public Integer getPropertyMetadataId() {
        return propertyMetadataId;
    }

    public void setPropertyMetadataId(Integer propertyMetadataId) {
        this.propertyMetadataId = propertyMetadataId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NwPropMetadata other = (NwPropMetadata) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (typeId == null) {
            if (other.typeId != null)
                return false;
        } else if (!typeId.equals(other.typeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwPropMetadata [propertyMetadataId=");
        builder.append(propertyMetadataId);
        builder.append(", typeId=");
        builder.append(typeId);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

}
