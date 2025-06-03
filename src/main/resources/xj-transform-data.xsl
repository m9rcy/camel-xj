<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <Sync creationDateTime="0001-01-01T00:00:00"
              baseLanguange="EN"
              version="1">
            <Loc>
                <xsl:attribute name="action">
                    <xsl:choose>
                        <xsl:when test="DataModelDto/changeAction = 'UPDATE' or DataModelDto/changeAction = 'RENAME'">AddChange</xsl:when>
                        <xsl:when test="DataModelDto/changeAction = 'ACTIVATE' or DataModelDto/changeAction = 'DEACTIVATE'">Change</xsl:when>
                        <xsl:otherwise>Unknown</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>

                <CHANGEDATE>
                    <xsl:value-of select="DataModelDto/modifiedOn"/>
                </CHANGEDATE>

                <DESCRIPTION>
                    <xsl:value-of select="DataModelDto/description"/>
                </DESCRIPTION>

                <LOCATION>
                    <xsl:value-of select="DataModelDto/name"/>
                </LOCATION>

                <STATUS>
                    <xsl:choose>
                        <xsl:when test="DataModelDto/active = 'true'">COMMISSIONED</xsl:when>
                        <xsl:otherwise>SCRAPPED</xsl:otherwise>
                    </xsl:choose>
                </STATUS>
            </Loc>
        </Sync>
    </xsl:template>

</xsl:stylesheet>
