package com.andrew;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房间实体
 *
 * @author tongwenjin
 * @since 2022 /9/15
 */

public class ZaRoom implements Serializable {

    public static final String TYPE_NORMAL = "1";

    public static final String TYPE_COMMON_AREA = "2";

    public static final String NAME_COMMON_AREA = "公共区域";

    private Integer id;

    private String name;

    private Long orgId;

    private String orgName;

    private Integer buildingId;

    private String buildingName;

    private Integer floorId;

    private BigDecimal floorNo;

    private String type;

    private String image;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets org id.
     *
     * @return the org id
     */
    public Long getOrgId() {
        return orgId;
    }

    /**
     * Sets org id.
     *
     * @param orgId the org id
     */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    /**
     * Gets org name.
     *
     * @return the org name
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * Sets org name.
     *
     * @param orgName the org name
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * Gets building id.
     *
     * @return the building id
     */
    public Integer getBuildingId() {
        return buildingId;
    }

    /**
     * Sets building id.
     *
     * @param buildingId the building id
     */
    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    /**
     * Gets building name.
     *
     * @return the building name
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Sets building name.
     *
     * @param buildingName the building name
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Gets floor id.
     *
     * @return the floor id
     */
    public Integer getFloorId() {
        return floorId;
    }

    /**
     * Sets floor id.
     *
     * @param floorId the floor id
     */
    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    /**
     * Gets floor no.
     *
     * @return the floor no
     */
    public BigDecimal getFloorNo() {
        return floorNo;
    }

    /**
     * Sets floor no.
     *
     * @param floorNo the floor no
     */
    public void setFloorNo(BigDecimal floorNo) {
        this.floorNo = floorNo;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets image.
     *
     * @param image the image
     */
    public void setImage(String image) {
        this.image = image;
    }
}
