----------------------------------------------------------------
-- Copyright (c) 2010-2011 Zipline Games, Inc. 
-- All Rights Reserved. 
-- http://getmoai.com
----------------------------------------------------------------
print(MOAIFlurry)
print (MOAIFlurryIOS)
MOAIFlurryIOS.startSession("APIKEY")

MOAIFlurry.logEvent("Starting App")

MOAISim.openWindow ( "test", 320, 480 )

viewport = MOAIViewport.new ()
viewport:setSize ( 320, 480 )
viewport:setScale ( 320, -480 )

layer = MOAILayer2D.new ()
layer:setViewport ( viewport )
MOAISim.pushRenderPass ( layer )

MOAIFlurry.logEvent("Halfway", {extradata="test"})

gfxQuad = MOAIGfxQuad2D.new ()
gfxQuad:setTexture ( "moai.png" )
gfxQuad:setRect ( -128, -128, 128, 128 )
gfxQuad:setUVRect ( 0, 0, 1, 1 )

prop = MOAIProp2D.new ()
prop:setDeck ( gfxQuad )
layer:insertProp ( prop )

MOAIFlurry.logTimedEvent('Spin', {prop='main'})
a= prop:moveRot ( 360, 1.5 )
a:setListener(MOAIAction.EVENT_STOP, function()
	MOAIFlurry.endTimedEvent('Spin')
end)
print(a)

