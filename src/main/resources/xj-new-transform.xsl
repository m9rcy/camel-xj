<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xj="http://camel.apache.org/component/xj"
                version="2.0">

    <xsl:param name="key.name" select="'OR100'"/>

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <Link creationDateTime="0001-01-01T00:00:00"
                baseLanguange="EN"
                version="1">
            <RequestSet>
                <!-- Process add operations -->
                <xsl:apply-templates select="//object[@xj:name='op' and text()='add']/parent::object"/>
                <!-- Process remove operations -->
                <xsl:apply-templates select="//object[@xj:name='op' and text()='remove']/parent::object"/>
            </RequestSet>
        </Link>
    </xsl:template>

    <xsl:template match="object">
        <RequestLink>
            <xsl:attribute name="action">
                <xsl:choose>
                    <xsl:when test="object[@xj:name='op']/text() = 'add'">AddedChange</xsl:when>
                    <xsl:otherwise>DeleteChange</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <Name><xsl:value-of select="$key.name"/></Name>
            <Value>
                <xsl:text>OW</xsl:text>
                <xsl:value-of select="object[@xj:name='value']/text()"/>
            </Value>
        </RequestLink>
    </xsl:template>

</xsl:stylesheet>