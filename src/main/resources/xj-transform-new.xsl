<xsl:stylesheet version="3.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="https://www.w3schools.com/prices"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                exclude-result-prefixes="xs fn">

    <!-- Parameter for the order number from header -->
    <xsl:param name="header.ordernum"/>

    <!-- Identity template to copy all nodes and attributes -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Main template to transform the JSON structure -->
    <xsl:template match="/">
        <m:Link xmlns:m="https://www.w3schools.com/prices"
                creationDateTime="0001-01-01T00:00:00"
                baseLanguange="EN"
                version="1">
            <m:RequestSet>
                <!-- Process added items -->
                <xsl:for-each select="json-to-xml(.)/fn:map/fn:array[@key='added']/fn:string">
                    <m:RequestLink action="AddedChange">
                        <m:Name><xsl:value-of select="$header.ordernum"/></m:Name>
                        <m:Value><xsl:value-of select="."/></m:Value>
                    </m:RequestLink>
                </xsl:for-each>

                <!-- Process deleted items -->
                <xsl:for-each select="json-to-xml(.)/fn:map/fn:array[@key='deleted']/fn:string">
                    <m:RequestLink action="DeleteChange">
                        <m:Name><xsl:value-of select="$header.ordernum"/></m:Name>
                        <m:Value><xsl:value-of select="."/></m:Value>
                    </m:RequestLink>
                </xsl:for-each>
            </m:RequestSet>
        </m:Link>
    </xsl:template>

</xsl:stylesheet>