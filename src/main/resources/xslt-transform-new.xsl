<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="https://www.w3schools.com/prices"
                version="1.0"
                exclude-result-prefixes="m">

    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="nameValue" select="'OR100'"/>

    <xsl:template match="/Root">
        <m:Link creationDateTime="0001-01-01T00:00:00" baseLanguange="EN" version="1">
            <m:RequestSet>
                <xsl:for-each select="Action[Value]">
                    <xsl:variable name="actionName" select="@name"/>
                    <xsl:for-each select="Value">
                        <m:RequestLink action="{$actionName}">
                            <m:Name><xsl:value-of select="$nameValue"/></m:Name>
                            <m:Value><xsl:value-of select="."/></m:Value>
                        </m:RequestLink>
                    </xsl:for-each>
                </xsl:for-each>
            </m:RequestSet>
        </m:Link>
    </xsl:template>

</xsl:stylesheet>
