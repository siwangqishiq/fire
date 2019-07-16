#version 300 es
precision highp float;				//����Ĭ�ϸ��㾫��
uniform highp sampler2D sTexture;	//������������
uniform highp vec3 uLightLocation;	//��Դλ��
uniform highp mat4 uMVPMatrixGY; 	//�ܱ任����(��Դ)
in vec4 ambient;				//���մӶ�����ɫ�������Ļ���������ǿ��
in vec4 diffuse; 				//���մӶ�����ɫ��������ɢ�������ǿ��
in vec4 specular; 				//���մӶ�����ɫ�������ľ��������ǿ��
in vec4 vPosition;  			//���մӶ�����ɫ�������ı任�󶥵�λ��
out vec4 fragColor;				//�������ƬԪ��ɫ
void main(){
	//��ƬԪ��λ��ͶӰ����Դ������������Ľ�ƽ����
   vec4 gytyPosition=uMVPMatrixGY * vec4(vPosition.xyz,1);
   gytyPosition=gytyPosition/gytyPosition.w;  		//����͸�ӳ���
   float s=(gytyPosition.s+1.0)/2.0; 				//��ͶӰ������껻��Ϊ��������
   float t=(gytyPosition.t+1.0)/2.0;
   float minDis=texture(sTexture, vec2(s,t)).r; 	//��ͶӰ����(��������)ͼ���в����õ���С����ֵZA
   float currDis=distance(vPosition.xyz,uLightLocation);//�����Դ����ƬԪ�ľ���ZB
   vec4 finalColor=vec4(0.95,0.95,0.95,1.0); 			//�������ɫ
   float a=currDis-3.0;
   const float p=0.8;
   const float doublep=1.6;
   if(s>=0.0&&s<=1.0&&t>=0.0&&t<=1.0) { 		//�����������ںϷ���Χ������ͶӰ��ͼ
   		if(minDis<=a-p) {				//��ʵ�ʾ��������С����������Ӱ��
   			fragColor=finalColor*ambient;
   		} else if(minDis>=a+p){
   			fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   		}else
   		{
   			vec4 color = finalColor*ambient;		//���û�������ɫ
   			vec4 color2 = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   			float b=(a+p-minDis)/doublep;
   			fragColor=b*color+(1.0-b)*color2;
   		}
   }else{ 	//������Ӱ��������ͨ��������ɫ
         fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   }
}
