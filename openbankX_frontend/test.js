const puppeteer = require('puppeteer');
(async () => {
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();
  
  page.on('console', msg => console.log('BROWSER_LOG:', msg.text()));
  page.on('pageerror', error => console.log('BROWSER_ERR:', error.message));
  page.on('response', response => {
    if(response.url().includes('/api/v1/')) {
       console.log('API_RESP:', response.url(), response.status());
    }
  });

  await page.goto('http://localhost:4200/login');
 
  await page.evaluate(() => localStorage.clear());
  
  await page.type('input[type="email"]', 'akash@openbankx.com');
  await page.type('input[type="password"]', 'Test@1234');
  await page.click('button[type="submit"]');
  

  await new Promise(r => setTimeout(r, 4000));
  
  const content = await page.content();
  if (content.includes('Loading consents')) {
     console.log('STILL LOADING');
  } else {
     console.log('NOT LOADING');
  }

  await browser.close();
})();
