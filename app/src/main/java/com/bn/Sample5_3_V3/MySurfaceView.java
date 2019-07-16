package com.bn.Sample5_3_V3;
import static com.bn.Sample5_3_V3.Constant.SCREEN_HEIGHT;
import static com.bn.Sample5_3_V3.Constant.SCREEN_WIDTH;
import static com.bn.Sample5_3_V3.Constant.SHADOW_TEX_HEIGHT;
import static com.bn.Sample5_3_V3.Constant.SHADOW_TEX_WIDTH;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
 
class MySurfaceView extends GLSurfaceView
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��
    
    private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����
    
    //�����λ�����
    float cx=0;
    float cy=20;
    float cz=50;
    float cAngle=0;
    final float cR=50;
    //�ƹ�λ��
    float lx=0;
    final float ly=10;
    float lz=85;
    float lAngle=0;
    final float lR=85;
    
    final float cDis=15;
    
    //��Դ�ܱ任����
    float[] mMVPMatrixGY;
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES3.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
        
        new Thread()
        {
        	public void run()
        	{
        		while(true)
        		{
        			lAngle += 0.5;//������x����ת�Ƕ�                    
                    lx=(float) Math.sin(Math.toRadians(lAngle))*lR;
                    lz=(float) Math.cos(Math.toRadians(lAngle))*lR;
                    try {
   					Thread.sleep(80);
	   				} catch (InterruptedException e) {
	   					e.printStackTrace();
	   				}
        		}
        	}
        }.start();
    }
	
	//�����¼��ص�����
    @SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            cAngle += dx * TOUCH_SCALE_FACTOR;//������x����ת�Ƕ�
            cx=(float) Math.sin(Math.toRadians(cAngle))*cR;
            cz=(float) Math.cos(Math.toRadians(cAngle))*cR;
            cy+= dy/10.0f;//������z���ƶ�
            requestRender();//�ػ滭��
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }

	private class SceneRenderer implements Renderer
    {
    	//��ָ����obj�ļ��м��ض���
		LoadedObjectVertexNormal lovo_pm;//ƽ��
		LoadedObjectVertexNormal lovo_ch;//���
		LoadedObjectVertexNormal lovo_cft;//������
		LoadedObjectVertexNormal lovo_qt;//����
		LoadedObjectVertexNormal lovo_yh;//Բ��

		int frameBufferId;
		int shadowId;// ��̬��������Ӱ����id
		int renderDepthBufferId;// ��̬��������Ӱ����id

		//��ʼ��֡�������Ⱦ����
		public void initFRBuffers()
		{
			int[] tia=new int[1];
			GLES30.glGenFramebuffers(1, tia, 0);
			frameBufferId=tia[0];
			
			GLES30.glGenRenderbuffers(1, tia, 0);
			renderDepthBufferId=tia[0];
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
        	GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER,
        			GLES30.GL_DEPTH_COMPONENT16, SHADOW_TEX_WIDTH, SHADOW_TEX_HEIGHT);
			
			int[] tempIds = new int[1];
    		GLES30.glGenTextures
    		(
    				1,          //����������id������
    				tempIds,   	//����id������
    				0           //ƫ����
    		);
    		
    		shadowId=tempIds[0];
    		//��ʼ����ɫ��������
        	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowId);        	
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); 
    		
    		GLES30.glTexImage2D
        	(
        		GLES30.GL_TEXTURE_2D,  
        		0, 
        		GLES30.GL_R16F,
        		SHADOW_TEX_WIDTH, 
        		SHADOW_TEX_HEIGHT, 
        		0, 
        		GLES30.GL_RED, 
        		GLES30.GL_FLOAT, 
        		null
        	);
        	
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            GLES30.glFramebufferTexture2D
            (
            	GLES30.GL_FRAMEBUFFER, 
            	GLES30.GL_COLOR_ATTACHMENT0,
            	GLES30.GL_TEXTURE_2D, 
            	shadowId, 
            	0
            );       
        	GLES30.glFramebufferRenderbuffer
        	(
        		GLES30.GL_FRAMEBUFFER,
        		GLES30.GL_DEPTH_ATTACHMENT,
        		GLES30.GL_RENDERBUFFER,
        		renderDepthBufferId
        	);
		}
		
        //ͨ�����Ʋ�����Ӱ����
        public void generateShadowImage()
        {
        	GLES30.glViewport(0, 0, SHADOW_TEX_WIDTH, SHADOW_TEX_HEIGHT);
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
        	//�����Ȼ�������ɫ����
        	GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        	
        	//���ô˷������������9����λ�þ���
        	MatrixState.setCamera(lx,ly,lz,0f,0f,0f,0f,1,0);
        	MatrixState.setProjectFrustum(-1, 1, -1.0f, 1.0f, 1.5f, 400);
        	mMVPMatrixGY=MatrixState.getViewProjMatrix();
        	
        	//�����������ƽ��
        	lovo_pm.drawSelfForShadow();
        	
        	//��������
        	MatrixState.pushMatrix();
        	MatrixState.translate(-cDis, 0, 0);
        	//�����ص����岿λ�����������
        	lovo_qt.drawSelfForShadow();
        	MatrixState.popMatrix();
        	
        	//����Բ��
        	MatrixState.pushMatrix();            
        	MatrixState.translate(cDis, 0, 0);
        	MatrixState.rotate(30, 0, 1, 0);
        	//�����ص����岿λ�����������
        	lovo_yh.drawSelfForShadow();
        	MatrixState.popMatrix();  
        	
        	//���Ƴ�����
        	MatrixState.pushMatrix(); 
        	MatrixState.translate(0, 0, -cDis);
        	//�����ص����岿λ�����������
        	lovo_cft.drawSelfForShadow();
        	MatrixState.popMatrix();
        	
        	//���Ʋ��
        	MatrixState.pushMatrix(); 
        	MatrixState.translate(0, 0, cDis);
        	//�����ص����岿λ�����������
        	lovo_ch.drawSelfForShadow();
        	MatrixState.popMatrix();     
        }
        
        public void drawScene(GL10 gl)
        {
        	//�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, (int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        	
        	//�����Ȼ�������ɫ����
        	GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        	//���ô˷������������9����λ�þ���
        	MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1f,0f);
        	MatrixState.setProjectFrustum(-ratio, ratio, -1.0f, 1.0f, 2, 1000);  
        	
        	//�����������ƽ��
        	lovo_pm.drawSelf(shadowId,mMVPMatrixGY);
        	
        	//��������
        	MatrixState.pushMatrix();
        	MatrixState.translate(-cDis, 0, 0);
        	//�����ص����岿λ�����������
        	lovo_qt.drawSelf(shadowId,mMVPMatrixGY);
        	MatrixState.popMatrix();
        	
        	//����Բ��
        	MatrixState.pushMatrix(); 
        	MatrixState.translate(cDis, 0, 0);
        	MatrixState.rotate(30, 0, 1, 0);
        	//�����ص����岿λ�����������
        	lovo_yh.drawSelf(shadowId,mMVPMatrixGY);
        	MatrixState.popMatrix();
        	
        	//���Ƴ�����
        	MatrixState.pushMatrix(); 
        	MatrixState.translate(0, 0, -cDis);
        	//�����ص����岿λ�����������
        	lovo_cft.drawSelf(shadowId,mMVPMatrixGY);
        	MatrixState.popMatrix();
        	
        	//���Ʋ��
        	MatrixState.pushMatrix(); 
        	MatrixState.translate(0, 0, cDis);
        	//�����ص����岿λ�����������
        	lovo_ch.drawSelf(shadowId,mMVPMatrixGY);
        	MatrixState.popMatrix();
        }
        long start=System.nanoTime();
        int count=0;
        public void onDrawFrame(GL10 gl)
        {
        	//����FPS=========================================================
        	count++;
        	if(count==150)
        	{
        		count=0;
        		long end=System.nanoTime();
        		System.out.println("FPS:"+(1000000000.0*150/(end-start)));
        		start=end;
        	}
        	//����FPS=========================================================
        	
        	MatrixState.setLightLocation(lx, ly, lz);     
        	//ͨ�����Ʋ�����Ӱ����
        	generateShadowImage();
        	drawScene(gl);
        }
        
        float ratio;
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
        	//�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
        	ratio = (float) width / height;  
        	Constant.SCREEN_HEIGHT=height;
        	Constant.SCREEN_WIDTH=width;
        	
            //��ʼ��֡����
            initFRBuffers();
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	//������Ļ����ɫRGBA
            GLES30.glClearColor(0f,0f,0f,1.0f);
            //����ȼ��
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //�򿪱������
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //��ʼ���任����
            MatrixState.setInitStack();
            //��ʼ����Դλ��
            MatrixState.setLightLocation(lx, ly, lz);
            //����Ҫ���Ƶ�����
            lovo_ch=LoadUtil.loadFromFileVertexOnly("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm=LoadUtil.loadFromFileVertexOnly("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_cft=LoadUtil.loadFromFileVertexOnly("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_qt=LoadUtil.loadFromFileVertexOnly("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_yh=LoadUtil.loadFromFileVertexOnly("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);       
        }
    }
}
