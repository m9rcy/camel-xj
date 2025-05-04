<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="https://www.w3schools.com/prices"
                xmlns:xj="http://camel.apache.org/component/xj"
                exclude-result-prefixes="xj">

    <xsl:output omit-xml-declaration="no" encoding="UTF-8" method="xml" indent="yes"/>

    <!-- Parameters with defaults -->
    <xsl:param name="creationDateTime" select="'2023-01-01T00:00:00'"/> <!-- Will be overridden -->
    <xsl:param name="baseLanguage" select="'EN'"/>
    <xsl:param name="version" select="'1'"/>

    <!-- Match root -->
    <xsl:template match="/">
        <m:GetPrice creationDateTime="{$creationDateTime}" baseLanguage="{$baseLanguage}" version="{$version}">
            <m:RequestSet>
                <m:Request>
                    <m:Name>
                        <xsl:value-of select="//object[@xj:name='id']"/>
                    </m:Name>
                    <m:Value>
                        <xsl:value-of select="//object[@xj:name='rarity']"/>
                    </m:Value>
                </m:Request>
            </m:RequestSet>
        </m:GetPrice>
    </xsl:template>

</xsl:stylesheet>
