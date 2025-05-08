<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xj="http://camel.apache.org/component/xj"
                exclude-result-prefixes="xj">

    <xsl:output method="xml" indent="yes"/>

    <!-- Helper named template to output ORNUMs based on a filter -->
    <xsl:template name="output-ornums">
        <xsl:param name="operation"/>
        <xsl:param name="arrayPath"/>
        <xsl:param name="indexedPath"/>

        <!-- Array-style -->
        <xsl:for-each select="object[
			object[@xj:name='op' and text()=$operation]
			and object[@xj:name='path' and text()=$arrayPath]
		]/object[@xj:name='value']/object">
            <ORNUM><xsl:value-of select="."/></ORNUM>
        </xsl:for-each>

        <!-- Indexed-style -->
        <xsl:for-each select="object[
			object[@xj:name='op' and text()=$operation]
			and object[@xj:name='path' and starts-with(text(), $indexedPath)]
			and object[@xj:name='value']
		]">
            <ORNUM><xsl:value-of select="object[@xj:name='value']"/></ORNUM>
        </xsl:for-each>
    </xsl:template>

    <!-- Main template -->
    <xsl:template match="/object">
        <Link creationDateTime="0001-01-01T00:00:00" baseLanguange="EN" version="1">
            <RequestSet>

                <!-- AddChange block only if at least one matching node -->
                <xsl:if test="object[
					object[@xj:name='op' and text()='add']
					and object[@xj:name='path' and (text()='/workOrderNumbers' or starts-with(text(), '/workOrderNumbers/'))]
				]">
                    <LinkOR action="AddChange">
                        <xsl:call-template name="output-ornums">
                            <xsl:with-param name="operation" select="'add'"/>
                            <xsl:with-param name="arrayPath" select="'/workOrderNumbers'"/>
                            <xsl:with-param name="indexedPath" select="'/workOrderNumbers/'"/>
                        </xsl:call-template>
                    </LinkOR>
                </xsl:if>

                <!-- Delete block only if at least one matching node -->
                <xsl:if test="object[
					object[@xj:name='op' and text()='remove']
					and object[@xj:name='path' and (text()='/workOrderNumbers' or starts-with(text(), '/workOrderNumbers/'))]
				]">
                    <LinkOR action="Delete">
                        <xsl:call-template name="output-ornums">
                            <xsl:with-param name="operation" select="'remove'"/>
                            <xsl:with-param name="arrayPath" select="'/workOrderNumbers'"/>
                            <xsl:with-param name="indexedPath" select="'/workOrderNumbers/'"/>
                        </xsl:call-template>
                    </LinkOR>
                </xsl:if>

            </RequestSet>
        </Link>
    </xsl:template>

</xsl:stylesheet>
