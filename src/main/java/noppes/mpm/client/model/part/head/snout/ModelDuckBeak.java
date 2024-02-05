package noppes.mpm.client.model.part.head.snout;

import net.minecraft.client.model.ModelRenderer;
import noppes.mpm.client.model.IModelMPM;
import org.lwjgl.opengl.GL11;

public class ModelDuckBeak extends ModelRenderer {
	ModelRenderer Top3;
	ModelRenderer Top2;
	ModelRenderer Bottom;
	ModelRenderer Left;
	ModelRenderer Right;
	ModelRenderer Middle;
	ModelRenderer Top;

	public ModelDuckBeak(IModelMPM base) {
		super(base.getBiped());
		
		Top3 = new ModelRenderer(base.getBiped(), 14, 0);
		Top3.addBox(0F, 0F, 0F, 2, 1, 3);
		Top3.setRotationPoint(-1F, -2F, -5F);
		setRotation(Top3, 0.3346075F, 0F, 0F);
		addChild(Top3);

		Top2 = new ModelRenderer(base.getBiped(), 0, 0);
		Top2.addBox(0F, 0F, -0.4F, 4, 1, 3);
		Top2.setRotationPoint(-2F, -3F, -2F);
		setRotation(Top2, 0.3346075F, 0F, 0F);
		addChild(Top2);

		Bottom = new ModelRenderer(base.getBiped(), 24, 0);
		Bottom.addBox(0F, 0F, 0F, 2, 1, 5);
		Bottom.setRotationPoint(-1F, -1F, -5F);
		addChild(Bottom);

		Left = new ModelRenderer(base.getBiped(), 0, 4);
		Left.mirror = true;
		Left.addBox(0F, 0F, 0F, 1, 3, 2);
		Left.setRotationPoint(0.98F, -3F, -2F);
		addChild(Left);

		Right = new ModelRenderer(base.getBiped(), 0, 4);
		Right.addBox(0F, 0F, 0F, 1, 3, 2);
		Right.setRotationPoint(-1.98F, -3F, -2F);
		addChild(Right);

		Middle = new ModelRenderer(base.getBiped(), 3, 0);
		Middle.addBox(0F, 0F, 0F, 2, 1, 3);
		Middle.setRotationPoint(-1F, -2F, -5F);
		addChild(Middle);

		Top = new ModelRenderer(base.getBiped(), 6, 4);
		Top.addBox(0F, 0F, 0F, 2, 2, 1);
		Top.setRotationPoint(-1F, -4.4F, -1F);
		addChild(Top);
	}
	
	@Override
    public void render(float f)
    {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, -1f * f);
		GL11.glScalef(0.82f, 0.82f, 0.70f);
    	super.render(f);
    	GL11.glPopMatrix();
    }

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
