### Bank 거래를 위한 RESTful API Server

#### Intro
> mustach open source 사용
> 
> 전달되는 tml data와 tml에 표현 할 data를 전달받아 pdf file 및 image file 생성
> Mac address를 기반으로 license validate
> 

#### 사용방법
```
- 요청
Map<String,Object> result = JMakeImage.getInstance().make(
                                      data, //데이터 문자열
                                      tml, //tml 문자열
                                      pdfPath, //생성 할 pdf의 경로
                                      imgPath, //생성 할 이미지의 경로
                                      fileName, //생성 할 pdf 및 이미지의 파일명
                                      isMac //mac license 여부
                                      );

- 응답
Map<String,Object> result의 담기는 값은 아래와 같다.
page : 총 이미지를 생성한 개수
type : 생성 된 이미지의 type
dpi : 생성 된 이미지의 dpi
delyn : 생성 된 pdf의 삭제 여부
file-[페이지번호] : 생성 된 이미지 파일의 경로

```

```
git clone https://github.com/suhojang/HTMLtoPdf-Image.git
```
