<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="https://www.w3schools.com/prices"
                xmlns:xj="http://camel.apache.org/component/xj"
                version="2.0">

    <xsl:param name="nameValue" select="'OR100'"/>

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <m:Link creationDateTime="0001-01-01T00:00:00"
                baseLanguange="EN"
                version="1">
            <m:RequestSet>
                <!-- Process add operations -->
                <xsl:apply-templates select="//object[@xj:name='op' and text()='add']/parent::object"/>
                <!-- Process remove operations -->
                <xsl:apply-templates select="//object[@xj:name='op' and text()='remove']/parent::object"/>
            </m:RequestSet>
        </m:Link>
    </xsl:template>

    <xsl:template match="object">
        <m:RequestLink>
            <xsl:attribute name="action">
                <xsl:choose>
                    <xsl:when test="object[@xj:name='op']/text() = 'add'">AddedChange</xsl:when>
                    <xsl:otherwise>DeleteChange</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <m:Name><xsl:value-of select="$nameValue"/></m:Name>
            <m:Value>
                <xsl:text>OW</xsl:text>
                <xsl:value-of select="object[@xj:name='value']/text()"/>
            </m:Value>
        </m:RequestLink>
    </xsl:template>

</xsl:stylesheet>