package com.samsung.nmt.cmenrichment.dto;

public class NwSubElKey {
    private Integer elementId;
    private Integer subElTypeId;
    private String name;

    public static NwSubElKey createKey(Integer elementId, Integer subElTypeId, String name) {
        return new NwSubElKey(elementId, subElTypeId, name);
    }

    private NwSubElKey(Integer elementId, Integer subElTypeId, String name) {
        super();
        this.elementId = elementId;
        this.subElTypeId = subElTypeId;
        this.name = name;
    }

    public Integer getElementId() {
        return elementId;
    }

    public Integer getSubElTypeId() {
        return subElTypeId;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((subElTypeId == null) ? 0 : subElTypeId.hashCode());
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
        NwSubElKey other = (NwSubElKey) obj;
        if (elementId == null) {
            if (other.elementId != null)
                return false;
        } else if (!elementId.equals(other.elementId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (subElTypeId == null) {
            if (other.subElTypeId != null)
                return false;
        } else if (!subElTypeId.equals(other.subElTypeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwSubElKey [elementId=");
        builder.append(elementId);
        builder.append(", subElTypeId=");
        builder.append(subElTypeId);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

}
